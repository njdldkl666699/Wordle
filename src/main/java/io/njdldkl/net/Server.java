package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.pojo.request.JoinRoomRequest;
import io.njdldkl.pojo.request.LeaveRoomRequest;
import io.njdldkl.pojo.request.StartGameRequest;
import io.njdldkl.pojo.response.HostLeftResponse;
import io.njdldkl.pojo.response.JoinRoomResponse;
import io.njdldkl.pojo.response.LeaveRoomResponse;
import io.njdldkl.pojo.response.StartGameResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Server {

    private ServerSocket serverSocket;

    // 连接的客户端所对应的TcpJsonHelper
    private final List<TcpJsonHelper> clientConnections = new ArrayList<>();

    public Server(User hostUser) {
        this.hostUser = hostUser;
        this.users.put(hostUser.getId(), hostUser);

        try {
            serverSocket = new ServerSocket(IntegerConstant.PORT);
            log.info("内置服务端在{}端口开启", IntegerConstant.PORT);

            new Thread(this::connectClient).start();

        } catch (IOException e) {
            log.error("服务器启动失败: {}", e.getMessage(), e);
        }
    }

    private void connectClient() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    log.error("接受客户端连接失败: {}", e.getMessage(), e);
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            SocketAddress address = clientSocket.getRemoteSocketAddress();
            log.info("客户端已连接: {}", address);
            TcpJsonHelper tcpHelper = new TcpJsonHelper(clientSocket, new ServerMessageHandler());
            tcpHelper.startReceiver();
            clientConnections.add(tcpHelper);
        } catch (Exception e) {
            log.info("客户端处理发生异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 关闭服务器
     */
    public void close() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("关闭服务器失败: {}", e.getMessage(), e);
            }
        }

        // 清空客户端连接
        clientConnections.clear();
    }

    private class ServerMessageHandler implements TcpJsonHelper.MessageHandler {
        @Override
        public void receiveMessage(JSONObject jsonObject, String type) {
            try {
                switch (type) {
                    case "JoinRoomRequest" -> joinRoom(jsonObject.toJavaObject(JoinRoomRequest.class));
                    case "LeaveRoomRequest" -> leaveRoom(jsonObject.toJavaObject(LeaveRoomRequest.class));
                    case "StartGameRequest" -> startGame(jsonObject.toJavaObject(StartGameRequest.class));
                    default -> log.warn("未知消息类型: {}", type);
                }
            } catch (IOException e) {
                log.error("处理消息时发生异常: ", e);
            }
        }

        @Override
        public void onError(Exception e) {
            if (e instanceof SocketException) {
                log.info("客户端连接已关闭: {}", e.getMessage());
            } else {
                log.error("发生异常: ", e);
            }
        }
    }

    // 房主用户
    private final User hostUser;

    // 房间内的用户列表
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    // 正确单词
    private Word answer;

    // 游戏开始时间
    private long startTime;
    // 游戏结束时间
    private long endTime;

    /**
     * 广播消息给所有连接的客户端
     */
    private void broadcastToAllClients(BaseMessage message) {
        List<TcpJsonHelper> invalidConnections = new ArrayList<>();

        for (TcpJsonHelper tcpHelper : clientConnections) {
            try {
                tcpHelper.sendMessage(message);
            } catch (IOException e) {
                // 记录无效连接，稍后移除
                log.info("向客户端发送消息失败，连接可能已关闭: {}", e.getMessage());
                invalidConnections.add(tcpHelper);
            }
        }

        // 移除无效连接
        if (!invalidConnections.isEmpty()) {
            clientConnections.removeAll(invalidConnections);
            log.info("已移除 {} 个无效连接", invalidConnections.size());
        }
    }

    /**
     * 加入房间
     */
    private void joinRoom(JoinRoomRequest request) throws IOException {
        User user = request.getUser();
        log.info("用户 {} 加入房间", user.getId());

        // 将用户添加到房间
        users.put(user.getId(), user);

        // 创建加入房间响应
        JoinRoomResponse joinRoomResponse = JoinRoomResponse.builder()
                .userList(new ArrayList<>(users.values()))
                .host(hostUser)
                .build();

        // 广播给所有连接的客户端
        broadcastToAllClients(joinRoomResponse);
    }

    /**
     * 离开房间
     */
    private void leaveRoom(LeaveRoomRequest request) throws IOException {
        log.info("用户 {} 离开房间", request);

        UUID userId = request.getUserId();
        if (userId.equals(hostUser.getId())) {
            log.info("房主 {} 离开房间", userId);
            // 如果房主离开，广播给所有用户
            broadcastToAllClients(new HostLeftResponse());
            // 关闭服务器
            close();
            log.info("服务器已关闭");
            return;
        }

        // 从房间中移除用户
        users.remove(userId);

        // 创建离开房间响应
        LeaveRoomResponse leaveRoomResponse =
                new LeaveRoomResponse(new ArrayList<>(users.values()));
        // 广播给所有连接的客户端
        broadcastToAllClients(leaveRoomResponse);
    }

    /**
     * 开始游戏
     */
    private void startGame(StartGameRequest request) {
        log.info("房主{}开始游戏，字母数量：{}", request.getUserId(), request.getLetterCount());
        // 检查房主是否是当前用户
        if (!request.getUserId().equals(hostUser.getId())) {
            log.warn("只有房主可以开始游戏");
            return;
        }
        // 广播给所有连接的客户端
        broadcastToAllClients(new StartGameResponse(request.getLetterCount()));
        // 记录游戏开始时间
        startTime = System.currentTimeMillis();
    }
}
