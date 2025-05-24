package io.njdldkl.service.impl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.net.Client;
import io.njdldkl.net.Server;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.pojo.event.GameStartedEvent;
import io.njdldkl.pojo.event.HostLeftEvent;
import io.njdldkl.pojo.event.UserListUpdatedEvent;
import io.njdldkl.service.PlayService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class MultiPlayService implements PlayService {

    // Guava EventBus 用于事件发布和订阅
    private final EventBus eventBus = new EventBus();
    private Client client;
    private Server server;
    private User hostUser;

    // 当前房间的用户列表
    private final List<User> users = new CopyOnWriteArrayList<>();

    // 注册事件监听器
    public void registerEventListener(Object listener) {
        eventBus.register(listener);
    }

    public void unregisterEventListener(Object listener) {
        eventBus.unregister(listener);
    }

    /**
     * 获取当前房间是否是房主
     */
    public boolean isHost(User user) {
        return hostUser != null && hostUser.getId().equals(user.getId());
    }

    @Override
    public void registerUser(User user, boolean host, String roomId) {
        try {
            // 如果为房主，创建房间，创建服务器
            if (host) {
                hostUser = user;
                server = new Server(user);
            }

            // 创建客户端，连接并等待加入房间响应
            client = new Client(user, roomId, eventBus);

            // 服务自身订阅事件，以维护内部状态
            eventBus.register(this);
        } catch (Exception e) {
            // 如果出错，返回只包含当前用户的列表
            log.error("注册用户失败: ", e);
            users.clear();
            users.add(user);
            eventBus.post(new UserListUpdatedEvent(users, host ? user : null));
        }
    }

    /**
     * 离开房间
     */
    public void leaveRoom(User user) {
        // 从用户列表中移除当前用户
        users.remove(user);

        // 通知服务器当前用户离开房间
        try {
            client.leaveRoom(user.getId());
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
        this.users.addAll(event.users());

        if (event.hostUser() != null) {
            this.hostUser = event.hostUser();
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
        hostUser = null;
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

    /**
     * 房主开始游戏，向服务器发生请求后，异步回调打开游戏界面
     */
    @Override
    public void requestStartGame(int letterCount) {
        try {
            client.startGame(hostUser.getId(), letterCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isValidWord(String word) {
        return false;
    }

    @Override
    public Pair<Boolean, List<WordStatus>> checkWord(String guessWord) {
        return null;
    }

    @Override
    public Word getAnswer() {
        return null;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

}
