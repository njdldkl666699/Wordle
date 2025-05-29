package io.njdldkl.service.impl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.njdldkl.enumerable.LetterStatus;
import io.njdldkl.net.Client;
import io.njdldkl.net.Server;
import io.njdldkl.pojo.*;
import io.njdldkl.pojo.event.*;
import io.njdldkl.service.PlayService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MultiPlayService implements PlayService {

    // Guava EventBus 用于事件发布和订阅
    private final EventBus eventBus = new EventBus();
    private Client client;
    private Server server;

    // 当前用户
    private User currentUser;

    // 房主的用户ID
    private UUID hostId;

    public boolean isHost(UUID userId) {
        return hostId != null && hostId.equals(userId);
    }

    // 当前房间的用户列表
    // 缓存用户名和头像（主要），减少头像的网络传输
    private final Map<UUID, User> users = new HashMap<>();

    // 注册事件监听器
    public void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    @Override
    public void registerUser(User user, boolean host, String roomId) {
        try {
            currentUser = user;
            // 如果为房主，创建房间，创建服务器
            if (host) {
                server = new Server(user);
            }

            // 创建客户端，连接并等待加入房间响应
            client = new Client(user, roomId, eventBus);
            currentUser = user;

            // 服务自身订阅事件，以维护内部状态
            eventBus.register(this);
        } catch (Exception e) {
            // 如果出错，返回只包含当前用户的列表
            log.error("注册用户失败: ", e);
            users.clear();
            users.put(currentUser.getId(), user);
            eventBus.post(new UserListUpdatedEvent((List<User>) users.values(), host ? user : null));
        }
    }

    /**
     * 离开房间
     */
    public void leaveRoom() {
        // 从用户列表中移除当前用户
        users.remove(currentUser.getId());

        // 通知服务器当前用户离开房间
        try {
            client.leaveRoom(currentUser.getId());
            // 关闭连接
            client.close();
            client = null;

            // 注销事件监听
            eventBus.unregister(this);

            log.info("离开房间成功，关闭连接");
        } catch (IOException e) {
            log.error("离开房间失败: ", e);
        }
    }

    /**
     * 订阅用户列表更新事件以更新服务内部状态
     */
    @Subscribe
    public void onUserListUpdated(UserListUpdatedEvent event) {
        this.users.clear();
        for (User user : event.users()) {
            this.users.put(user.getId(), user);
        }

        User hostUser = event.hostUser();
        if (hostUser != null) {
            this.hostId = hostUser.getId();
        }

        log.info("服务内部状态已更新: 用户数={}", users.size());
    }

    /**
     * 订阅房主离开事件
     */
    @Subscribe
    public void onHostLeft(HostLeftEvent event) {
        log.info("收到房主离开事件");

        // 关闭客户端连接
        if (client != null) {
            client.close();
            client = null;
            log.info("客户端连接已关闭");
        }

        // 如果当前实例是房主，同时关闭服务器
        if (server != null) {
            try {
                server.close();
                log.info("服务器实例已关闭");
            } catch (Exception e) {
                log.error("关闭服务器实例出错", e);
            } finally {
                server = null;
            }
        }

        // 清空状态
        currentUser = null;
        users.clear();

        // 取消事件订阅
        try {
            eventBus.unregister(this);
            log.info("已取消事件订阅");
        } catch (IllegalArgumentException e) {
            // 可能已经取消注册，忽略异常
            log.debug("事件总线取消注册异常，可能已经取消过", e);
        }

        log.info("房主离开处理完成");
    }

    // 当前猜测次数
    private int currentGuessCount;

    // 最大猜测次数
    private int maxGuessCount;

    /**
     * 房主开始游戏，向服务器发生请求后，异步回调打开游戏界面
     */
    @Override
    public void requestStartGame(int letterCount) {
        if (!isHost(currentUser.getId())) {
            return;
        }

        try {
            client.startGame(currentUser.getId(), letterCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onGameStarted(GameStartedEvent event) {
        currentGuessCount = 0;
        maxGuessCount = event.letterCount() + 1;
    }

    @Override
    public boolean isValidWord(String word) {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("客户端未连接或已关闭");
        }

        try {
            return client.isValidWord(currentUser.getId(), word);
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pair<Boolean, List<LetterStatus>> checkWord(String guessWord) {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("客户端未连接或已关闭");
        }

        try {
            var pairResult = client.checkWord(currentUser.getId(), guessWord);
            currentGuessCount++;
            return pairResult;
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 请求获取当前的游戏状态列表
     */
    public void requestPlayStatesUpdate(){
        if (client == null || !client.isConnected()) {
            log.warn("客户端未连接或已关闭，无法请求游戏状态");
            return;
        }

        try {
            // 向服务器请求当前游戏状态
            client.requestPlayStatesUpdate(currentUser.getId());
        } catch (IOException e) {
            log.error("请求游戏状态列表失败: ", e);
        }
    }

    @Subscribe
    public void onPlayStateListUpdated(PlayStateListUpdatedEvent event) {
        List<PlayStateVO> playStateVOList = new ArrayList<>();

        Map<UUID, PlayState> userPlayStates = event.playStateList();
        for (UUID userId : userPlayStates.keySet()) {
            PlayState playState = userPlayStates.get(userId);
            User user = users.get(userId);

            // 封装成VO
            PlayStateVO playStateVO = PlayStateVO.builder()
                    .name(user.getName())
                    .avatar(user.getAvatar())
                    .correctCount(playState.getCorrectCount())
                    .wrongPositionCount(playState.getWrongPositionCount())
                    .build();
            playStateVOList.add(playStateVO);
        }

        // 发布事件，通知view层更新
        eventBus.post(new PlayStateListShowEvent(playStateVOList));
    }

    @Override
    public Word getAnswer() {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("客户端未连接或已关闭");
        }

        try {
            return client.getAnswer(currentUser.getId());
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFailed() {
        return currentGuessCount >= maxGuessCount;
    }

}
