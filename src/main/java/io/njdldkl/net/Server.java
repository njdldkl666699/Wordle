package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@Slf4j
public class Server {

    private TcpJsonHelper tcpHelper;

    // 房主用户
    private User hostUser;

    // 房间内的用户列表
    private List<User> users;

    public Server(User hostUser) {
        this.hostUser = hostUser;
        this.users = List.of(hostUser);

        try (ServerSocket serverSocket = new ServerSocket(IntegerConstant.PORT)) {
            log.info("内置服务端在{}端口开启", IntegerConstant.PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            log.info("客户端已连接: {}", clientSocket.getRemoteSocketAddress());
            TcpJsonHelper tcpHelper = new TcpJsonHelper(clientSocket, new ServerMessageHandler());
            tcpHelper.startReceiver();

            // 保持连接直到客户端断开
            while (!clientSocket.isClosed()) {
            }
        } catch (Exception e) {
            log.info("客户端处理发生异常: {}", e.getMessage(), e);
        }
    }

    private class ServerMessageHandler implements MessageHandler {
        @Override
        public void handleMessage(JSONObject json) {
            // TODO 处理接收到的消息
            log.info("从客户端接收到数据: {}", json.toJSONString());
        }

        @Override
        public void onError(Exception e) {
            log.error("发生异常: {}", e.getMessage(), e);
        }
    }
}
