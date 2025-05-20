package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.request.JoinRoomRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class Client {

    private Socket serverSocket;
    private boolean isConnected = false;

    private TcpJsonHelper tcpHelper;
    private ClientMessageHandler messageHandler;

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
        // TODO 实现房间ID解析逻辑
        String host = roomId;
        serverSocket = new Socket(host, IntegerConstant.PORT);
        log.info("连接到服务器： {}:{}", host, IntegerConstant.PORT);

        try {
            messageHandler = new ClientMessageHandler();
            tcpHelper = new TcpJsonHelper(serverSocket, messageHandler);
            tcpHelper.startReceiver();
            isConnected = true;

            // 发送连接房间的请求
            JoinRoomRequest joinRoomRequest = new JoinRoomRequest()
            tcpHelper.sendJsonFromObject(joinRoomRequest);
        } catch (Exception e) {
            close();
            throw new IOException("初始化连接失败", e);
        }
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


    /**
     * 默认消息处理器
     */
    private class ClientMessageHandler implements MessageHandler {
        @Override
        public void handleMessage(JSONObject response) {
            log.info("收到服务器消息: {}", response.toString(JSONWriter.Feature.PrettyFormat));
            // TODO 根据不同的响应进行不同处理
        }

        @Override
        public void onError(Exception e) {
            log.error("消息处理错误: {}", e.getMessage(), e);
            // TODO　如果有等待中的请求，让它们失败
        }
    }
}
