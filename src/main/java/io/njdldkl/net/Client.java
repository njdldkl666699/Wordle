package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.request.JoinRoomRequest;
import io.njdldkl.pojo.request.LeaveRoomRequest;
import io.njdldkl.pojo.response.JoinRoomResponse;
import io.njdldkl.pojo.response.LeaveRoomResponse;
import io.njdldkl.util.IpNumberUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Client {

    private Socket serverSocket;
    private boolean isConnected = false;

    private TcpJsonHelper tcpHelper;
    private ClientMessageHandler messageHandler;

    // 用户列表更新的监听器
    @Setter
    private UserListUpdateListener userListUpdateListener;

    // 房主离开房间的监听器
    @Setter
    private Runnable hostLeftListener;

    // 用户列表更新的监听器接口
    public interface UserListUpdateListener {
        void onUpdated(List<User> users, User hostUser);
    }

    public Client(User user, String roomId) {
        try {
            connect(user, roomId);
        } catch (IOException e) {
            log.error("连接服务器失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 连接到服务器
     */
    public void connect(User user, String roomId) throws IOException {
        // 通过房间ID解析服务器地址，端口已知
        String host = IpNumberUtils.roomIdToAddress(roomId);
        serverSocket = new Socket(host, IntegerConstant.PORT);
        log.info("连接到服务器： {}:{}", host, IntegerConstant.PORT);

        try {
            messageHandler = new ClientMessageHandler();
            tcpHelper = new TcpJsonHelper(serverSocket, messageHandler);
            tcpHelper.startReceiver();
            isConnected = true;

            // 发送连接房间的请求
            JoinRoomRequest joinRoomRequest = new JoinRoomRequest(user, roomId);
            tcpHelper.sendMessage(joinRoomRequest);
        } catch (Exception e) {
            close();
            throw new IOException("初始化连接失败", e);
        }
    }

    /**
     * 离开房间
     *
     * @param userId 用户ID
     */
    public void leaveRoom(UUID userId) throws IOException {
        log.info("离开房间: {}", userId);
        tcpHelper.sendMessage(new LeaveRoomRequest(userId));
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (tcpHelper != null) {
            tcpHelper.close();
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                log.info("客户端连接已关闭");
            } catch (IOException e) {
                log.error("关闭连接时出错: {}", e.getMessage(), e);
            }
        }

        isConnected = false;
    }

    /**
     * 检查连接状态
     */
    public boolean isConnected() {
        return isConnected && serverSocket != null && !serverSocket.isClosed();
    }

    private void onJoinRoomResponse(JoinRoomResponse response) {
        log.info("加入房间成功: {}", response);
        User hostUser = response.getHost();
        List<User> users = response.getUserList();

        // 如果有监听器，通知用户列表已更新
        if (userListUpdateListener != null) {
            userListUpdateListener.onUpdated(users, hostUser);
        }
    }

    private void onLeaveRoomResponse(LeaveRoomResponse response) {
        log.info("离开房间成功: {}", response);
        List<User> users = response.getUserList();
        // 如果有监听器，通知用户列表已更新
        if (userListUpdateListener != null) {
            userListUpdateListener.onUpdated(users, null);
        }
    }

    private void onHostLeft() {
        log.info("房主已离开房间");
        if (hostLeftListener != null) {
            hostLeftListener.run();
        }
    }

    /**
     * 默认消息处理器
     */
    private class ClientMessageHandler implements TcpJsonHelper.MessageHandler {
        @Override
        public void receiveMessage(JSONObject jsonObject, String type) {
            switch (type) {
                case "JoinRoomResponse" -> onJoinRoomResponse(jsonObject.toJavaObject(JoinRoomResponse.class));
                case "LeaveRoomResponse" -> onLeaveRoomResponse(jsonObject.toJavaObject(LeaveRoomResponse.class));
                case "HostLeftResponse" -> onHostLeft();
            }
        }

        @Override
        public void onError(Exception e) {
            log.error("消息处理错误: ", e);
            // 如果有等待中的请求，让它们失败
        }
    }
}
