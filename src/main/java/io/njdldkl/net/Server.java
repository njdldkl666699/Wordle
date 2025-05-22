package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.request.JoinRoomRequest;
import io.njdldkl.pojo.response.JoinRoomResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    // 房主用户
    private final User hostUser;

    // 房间内的用户列表
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

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
            log.info("客户端已连接: {}", clientSocket.getRemoteSocketAddress());
            TcpJsonHelper tcpHelper = new TcpJsonHelper(clientSocket, new ServerMessageHandler());
            tcpHelper.startReceiver();
            clientConnections.add(tcpHelper);

            // 思考：需要吗
            // 保持连接直到客户端断开
//            while (!clientSocket.isClosed()) {
//                Thread.sleep(1000); // 减少CPU使用率
//            }
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

        // 关闭所有客户端连接
        for (TcpJsonHelper tcpHelper : clientConnections) {
            tcpHelper.close();
        }
        clientConnections.clear();
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
                .users(new ArrayList<>(users.values()))
                .host(hostUser)
                .build();

        // 广播给所有连接的客户端
        broadcastToAllClients(joinRoomResponse);
    }

    /**
     * 广播消息给所有连接的客户端
     */
    private void broadcastToAllClients(BaseMessage message) throws IOException {
        for (TcpJsonHelper tcpHelper : clientConnections) {
            tcpHelper.sendMessage(message);
        }
    }

    private class ServerMessageHandler implements TcpJsonHelper.MessageHandler {
        @Override
        public void receiveMessage(JSONObject jsonObject, String type) {
            log.debug("从客户端接收到数据: {}", jsonObject);
            try {
                switch (type) {
                    case "JoinRoomRequest" -> joinRoom(jsonObject.toJavaObject(JoinRoomRequest.class));
                    case "LeaveRoomRequest" -> {
                        // 处理离开房间请求
                        log.info("处理离开房间请求: {}", jsonObject);
                    }
                    default -> log.warn("未知消息类型: {}", type);
                }
            } catch (IOException e) {
                log.error("处理消息时发生异常: {}", e.getMessage(), e);
            }
        }

        @Override
        public void onError(Exception e) {
            log.error("发生异常: ", e);
        }
    }
}
