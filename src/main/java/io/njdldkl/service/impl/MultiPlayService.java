package io.njdldkl.service.impl;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.net.Client;
import io.njdldkl.net.Server;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.PlayService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MultiPlayService implements PlayService {

    private Client client;
    private Server server;
    private User hostUser;
    
    // 当前房间的用户列表
    private final List<User> users = new CopyOnWriteArrayList<>();
    
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

    @Override
    public void registerUser(User user, boolean host, String roomId) {
        try {
            // 如果为房主，创建房间
            if (host) {
                hostUser = user;
                server = new Server(user);
            }

            // 创建客户端，连接并等待加入房间响应
            client = new Client(user, roomId);

            // 设置用户列表更新监听器
            client.setUserListUpdateListener((users, hostUser) -> {
                // 更新当前用户列表
                this.users.clear();
                this.users.addAll(users);
                this.hostUser = hostUser;

                // 通知所有注册的回调
                for (Consumer<List<User>> callback : userListUpdateCallbacks) {
                    callback.accept(users);
                }
            });
        } catch (Exception e) {
            // 如果超时或出错，返回只包含当前用户的列表
            List<User> fallbackList = new ArrayList<>();
            fallbackList.add(user);
            users.clear();
            users.addAll(fallbackList);
        }
    }

    /**
     * 获取当前房间是否是房主
     */
    public boolean isHost(User user) {
        return hostUser != null && hostUser.getId().equals(user.getId());
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
