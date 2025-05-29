package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.eventbus.EventBus;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.enumerable.LetterStatus;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.PlayState;
import io.njdldkl.pojo.event.*;
import io.njdldkl.pojo.request.*;
import io.njdldkl.pojo.response.*;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.util.IpRoomIdUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
        log.info("连接到服务器：{}:{}", host, IntegerConstant.PORT);

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
     * 离开房间（异步）
     *
     * @param userId 用户ID
     */
    public void leaveRoom(UUID userId) throws IOException {
        log.info("离开房间: {}", userId);
        tcpHelper.sendMessage(new LeaveRoomRequest(userId));
    }

    /**
     * 房主开始游戏（异步）
     */
    public void startGame(UUID userId, int letterCount) throws IOException {
        log.info("房主{}开始游戏，字母数量：{}", userId, letterCount);
        tcpHelper.sendMessage(new StartGameRequest(userId, letterCount));
    }

    // 同步方式的请求下，等待结果的messageId与Future映射
    private final Map<UUID, CompletableFuture<?>> pendingFutures = new ConcurrentHashMap<>();

    /**
     * 检查单词是否有效（同步）
     */
    public boolean isValidWord(UUID userId, String word) throws IOException, ExecutionException, InterruptedException {
        log.info("检查单词是否存在: {}", word);
        var request = new ValidateWordRequest(userId, word);
        UUID messageId = request.getMessageId();
        pendingFutures.put(messageId, new CompletableFuture<Boolean>());
        tcpHelper.sendMessage(request);
        return (boolean) pendingFutures.get(messageId).get();
    }

    /**
     * 检查单词（同步）
     */
    public Pair<Boolean, List<LetterStatus>> checkWord(UUID userId, String guessWord)
            throws IOException, ExecutionException, InterruptedException {
        log.info("检查单词: {}", guessWord);
        var request = new CheckWordRequest(userId, guessWord);
        UUID messageId = request.getMessageId();
        pendingFutures.put(messageId, new CompletableFuture<Pair<Boolean, List<LetterStatus>>>());
        tcpHelper.sendMessage(request);
        return (Pair<Boolean, List<LetterStatus>>) pendingFutures.get(messageId).get();
    }

    /**
     * <p>获取答案</p>
     * 以同步方式获取答案，获取过程中会阻塞当前线程
     */
    public Word getAnswer(UUID userId) throws IOException, ExecutionException, InterruptedException {
        log.info("获取答案");
        var request = new GetAnswerRequest(userId);
        UUID messageId = request.getMessageId();
        pendingFutures.put(messageId, new CompletableFuture<Word>());
        tcpHelper.sendMessage(request);
        // 阻塞等待结果，转换为同步调用
        return (Word) pendingFutures.get(messageId).get();
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
                case "HostLeftResponse" -> onHostLeft(jsonObject.toJavaObject(HostLeftResponse.class));
                case "StartGameResponse" -> onStartGame(jsonObject.toJavaObject(StartGameResponse.class));
                case "ValidateWordResponse" -> onValidateWord(jsonObject.toJavaObject(ValidateWordResponse.class));
                case "CheckWordResponse" -> onCheckWord(jsonObject.toJavaObject(CheckWordResponse.class));
                case "GetAnswerResponse" -> onGetAnswerResponse(jsonObject.toJavaObject(GetAnswerResponse.class));
                case "PlayStatesUpdatedResponse" ->
                        onPlayStatesUpdated(jsonObject.toJavaObject(PlayStatesUpdatedResponse.class));
                case "GameOverResponse" -> onGameOver(jsonObject.toJavaObject(GameOverResponse.class));
                default -> log.warn("未知消息类型: {}", type);
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

    private void onHostLeft(HostLeftResponse response) {
        log.info("房主已离开房间");

        // 直接发布事件到EventBus
        eventBus.post(new HostLeftEvent());
    }

    private void onStartGame(StartGameResponse response) {
        log.info("游戏开始: {}", response);

        // 直接发布事件到EventBus
        eventBus.post(new GameStartedEvent(response.getLetterCount()));
    }

    private void onValidateWord(ValidateWordResponse response) {
        log.info("单词验证结果: {}", response);

        // 查找对应的Future
        UUID messageId = response.getMessageId();
        var future = (CompletableFuture<Boolean>) pendingFutures.get(messageId);
        if (future != null) {
            future.complete(response.isValid());
        } else {
            log.warn("未找到对应的单词验证请求: {}", messageId);
        }
    }

    private void onCheckWord(CheckWordResponse response) {
        log.info("单词检查结果: {}", response);

        // 查找对应的Future
        UUID messageId = response.getMessageId();
        var future = (CompletableFuture<Pair<Boolean, List<LetterStatus>>>) pendingFutures.get(messageId);
        if (future != null) {
            future.complete(new Pair<>(response.isCorrect(), response.getStatusList()));
        } else {
            log.warn("未找到对应的单词检查请求: {}", messageId);
        }
    }

    private void onPlayStatesUpdated(PlayStatesUpdatedResponse response) {
        log.info("用户游戏状态列表更新: {}", response);
        Map<UUID, PlayState> playStates = response.getPlayStates();

        // 发布用户游戏状态更新事件到eventBus
        // 转发给service来处理
        eventBus.post(new PlayStateListUpdatedEvent(playStates));
    }

    private void onGetAnswerResponse(GetAnswerResponse response) {
        UUID messageId = response.getMessageId();
        log.info("收到答案响应 messageId={}", messageId);

        // 查找对应的Future
        var future = (CompletableFuture<Word>) pendingFutures.get(messageId);
        if (future != null) {
            future.complete(response.getAnswer());
        } else {
            log.warn("未找到对应的答案请求: {}", messageId);
        }
    }

    private void onGameOver(GameOverResponse response) {
        log.info("游戏结束: {}", response);

        // 发布游戏结束事件到eventBus
        eventBus.post(new GameOverEvent(response.getAnswer(), response.getWinner(), response.getWinnerDuration()));
    }
}
