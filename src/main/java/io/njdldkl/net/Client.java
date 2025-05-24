package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.eventbus.EventBus;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.response.GetAnswerResponse;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.pojo.event.GameStartedEvent;
import io.njdldkl.pojo.event.HostLeftEvent;
import io.njdldkl.pojo.event.UserListUpdatedEvent;
import io.njdldkl.pojo.request.GetAnswerRequest;
import io.njdldkl.pojo.request.JoinRoomRequest;
import io.njdldkl.pojo.request.LeaveRoomRequest;
import io.njdldkl.pojo.request.StartGameRequest;
import io.njdldkl.pojo.response.JoinRoomResponse;
import io.njdldkl.pojo.response.LeaveRoomResponse;
import io.njdldkl.pojo.response.StartGameResponse;
import io.njdldkl.util.IpRoomIdUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Client {

    private Socket serverSocket;
    private boolean isConnected = false;

    private TcpJsonHelper tcpHelper;
    private ClientMessageHandler messageHandler;

    public Client(User user, String roomId, EventBus eventBus) {
        this.eventBus = eventBus;
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
        String host = IpRoomIdUtils.roomIdToIp(roomId);
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
     * 检查连接状态
     */
    public boolean isConnected() {
        return isConnected && serverSocket != null && !serverSocket.isClosed();
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
     * 房主开始游戏
     */
    public void startGame(UUID userId, int letterCount) throws IOException {
        log.info("房主{}开始游戏，字母数量：{}", userId, letterCount);
        tcpHelper.sendMessage(new StartGameRequest(userId, letterCount));
    }

    // 答案的Future对象
    private CompletableFuture<Word> answerFuture;

    /**
     * <p>获取答案</p>
     * 以同步方式获取答案，获取过程中会阻塞当前线程
     */
    public Word getAnswer() throws IOException, ExecutionException, InterruptedException {
        log.info("获取答案");
        answerFuture = new CompletableFuture<>();
        tcpHelper.sendMessage(new GetAnswerRequest());
        // 阻塞等待结果，转换为同步调用
        return answerFuture.get();
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
     * 默认消息处理器
     */
    private class ClientMessageHandler implements TcpJsonHelper.MessageHandler {
        @Override
        public void receiveMessage(JSONObject jsonObject, String type) {
            switch (type) {
                case "JoinRoomResponse" -> onJoinRoomResponse(jsonObject.toJavaObject(JoinRoomResponse.class));
                case "LeaveRoomResponse" -> onLeaveRoomResponse(jsonObject.toJavaObject(LeaveRoomResponse.class));
                case "HostLeftResponse" -> onHostLeft();
                case "StartGameResponse" -> onStartGame(jsonObject.toJavaObject(StartGameResponse.class));
                case "GetAnswerResponse" -> onGetAnswerResponse(jsonObject.toJavaObject(GetAnswerResponse.class));
            }
        }

        @Override
        public void onError(Exception e) {
            log.error("消息处理错误: ", e);
            // 如果有等待中的请求，让它们失败
        }
    }

    // 与MultiPlayService共享一个事件总线
    private final EventBus eventBus;

    private void onJoinRoomResponse(JoinRoomResponse response) {
        log.info("加入房间成功: {}", response);
        User hostUser = response.getHost();

        // 直接发布事件到EventBus
        eventBus.post(new UserListUpdatedEvent(response.getUserList(), hostUser));
    }

    private void onLeaveRoomResponse(LeaveRoomResponse response) {
        log.info("离开房间成功: {}", response);

        // 直接发布事件到EventBus
        eventBus.post(new UserListUpdatedEvent(response.getUserList(), null));
    }

    private void onHostLeft() {
        log.info("房主已离开房间");

        // 直接发布事件到EventBus
        eventBus.post(new HostLeftEvent());
    }

    private void onStartGame(StartGameResponse response) {
        log.info("游戏开始: {}", response);

        // 直接发布事件到EventBus
        eventBus.post(new GameStartedEvent(response.getLetterCount()));
    }

    private void onGetAnswerResponse(GetAnswerResponse response) {
        log.info("获取答案成功: {}", response);
        if (answerFuture != null) {
            // 将答案设置为完成状态
            answerFuture.complete(response.getAnswer());
            // 清空Future对象
            answerFuture = null;
        }
    }
}
