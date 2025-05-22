package io.njdldkl.service.impl;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.net.Client;
import io.njdldkl.net.Server;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.PlayService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Slf4j
public class MultiPlayService implements PlayService {

    private Client client;
    private Server server;
    private User hostUser;

    // 当前房间的用户列表
    private final List<User> users = new CopyOnWriteArrayList<>();

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
            client = new Client(user, roomId);
            // 设置各种监听器
            setupListeners();
        } catch (Exception e) {
            // 如果出错，返回只包含当前用户的列表
            log.error("注册用户失败: ", e);
            users.clear();
            users.add(user);
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
            log.info("离开房间成功，关闭连接");
        } catch (IOException e) {
            log.error("离开房间失败: ", e);
        }
    }

    /**
     * 设置各种监听器
     */
    private void setupListeners() {
        // 设置用户列表更新监听器
        client.setUserListUpdateListener((users, hostUser) -> {
            // 更新当前用户列表
            this.users.clear();
            this.users.addAll(users);
            if (hostUser != null) {
                this.hostUser = hostUser;
            }

            // 通知所有注册的回调
            for (Consumer<List<User>> callback : userListUpdateCallbacks) {
                callback.accept(users);
            }
        });

        // 设置房主离开的监听器
        client.setHostLeftListener(() -> {
            log.info("收到房主离开通知");
            // 通知所有注册的回调
            for (Runnable callback : hostLeftCallbacks) {
                callback.run();
            }
            // 关闭客户端连接
            client.close();
            client = null;
            log.info("关闭连接");
        });
    }

    // 用户列表更新的回调
    private final List<Consumer<List<User>>> userListUpdateCallbacks = new ArrayList<>();

    /**
     * 添加用户列表更新的回调
     */
    public void addUserListUpdateCallback(Consumer<List<User>> callback) {
        userListUpdateCallbacks.add(callback);
    }

    /**
     * 删除用户列表更新的回调
     */
    public void removeUserListUpdateCallback(Consumer<List<User>> callback) {
        userListUpdateCallbacks.remove(callback);
    }

    // 房主离开的回调
    private final List<Runnable> hostLeftCallbacks = new ArrayList<>();

    /**
     * 添加房主离开的回调
     */
    public void addHostLeftCallback(Runnable callback) {
        hostLeftCallbacks.add(callback);
    }

    /**
     * 删除房主离开的回调
     */
    public void removeHostLeftCallback(Runnable callback) {
        hostLeftCallbacks.remove(callback);
    }

    @Override
    public void startGame(int wordLength) {

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
