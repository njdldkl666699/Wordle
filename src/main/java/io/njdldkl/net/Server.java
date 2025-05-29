package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.enumerable.LetterStatus;
import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.PlayState;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.pojo.request.*;
import io.njdldkl.pojo.response.*;
import io.njdldkl.util.WordUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Server {

    private ServerSocket serverSocket;

    // 连接的客户端所对应的用户id与TcpJsonHelper的映射
    private final Map<UUID, TcpJsonHelper> clientConnections = new HashMap<>();
    // 最后一个没有和用户绑定的连接
    private TcpJsonHelper lastConnection;

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
                SocketAddress address = clientSocket.getRemoteSocketAddress();
                log.info("客户端已连接: {}", address);

                lastConnection = new TcpJsonHelper(clientSocket, new ServerMessageHandler());
                lastConnection.startReceiver();
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    log.error("接受客户端连接失败: {}", e.getMessage(), e);
                }
            }
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
            switch (type) {
                case "JoinRoomRequest" -> joinRoom(jsonObject.toJavaObject(JoinRoomRequest.class));
                case "LeaveRoomRequest" -> leaveRoom(jsonObject.toJavaObject(LeaveRoomRequest.class));
                case "StartGameRequest" -> startGame(jsonObject.toJavaObject(StartGameRequest.class));
                case "PlayStatesUpdateRequest" ->
                        updatePlayStates(jsonObject.toJavaObject(PlayStatesUpdateRequest.class));
                case "ValidateWordRequest" -> validateWord(jsonObject.toJavaObject(ValidateWordRequest.class));
                case "CheckWordRequest" -> checkWord(jsonObject.toJavaObject(CheckWordRequest.class));
                case "GetAnswerRequest" -> getAnswer(jsonObject.toJavaObject(GetAnswerRequest.class));
                default -> log.warn("未知消息类型: {}", type);
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

    // 用户游戏状态列表
    private final Map<UUID, PlayState> userPlayStates = new ConcurrentHashMap<>();
    // 用户键盘状态（用于统计字母状态）
    private final Map<UUID, LetterStatus[]> userKeyBoards = new ConcurrentHashMap<>();

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
        for (TcpJsonHelper tcpHelper : clientConnections.values()) {
            try {
                tcpHelper.sendMessage(message);
            } catch (IOException e) {
                log.error("发送消息失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 加入房间（广播）
     */
    private void joinRoom(JoinRoomRequest request) {
        User user = request.getUser();
        log.info("用户 {} 加入房间", request.getUserId());

        // 将最后一个未绑定的连接与用户绑定
        if (lastConnection != null) {
            clientConnections.put(request.getUserId(), lastConnection);
            lastConnection = null;
        }

        // 将用户添加到房间
        users.put(request.getUserId(), user);

        // 创建加入房间响应
        JoinRoomResponse joinRoomResponse = JoinRoomResponse.builder()
                .userList(new ArrayList<>(users.values()))
                .host(hostUser)
                .build();
        // 设置与请求相同的消息ID
        joinRoomResponse.setMessageId(request.getMessageId());

        // 广播给所有连接的客户端
        broadcastToAllClients(joinRoomResponse);
    }

    /**
     * 离开房间（广播）
     */
    private void leaveRoom(LeaveRoomRequest request) {
        UUID userId = request.getUserId();
        log.info("用户 {} 离开房间", userId);

        // 从房间中移除用户
        users.remove(userId);
        // 从连接列表中移除用户
        clientConnections.remove(userId);

        if (userId.equals(hostUser.getId())) {
            log.info("房主 {} 离开房间", userId);
            // 如果房主离开，广播给所有用户
            broadcastToAllClients(new HostLeftResponse());
            // 关闭服务器
            close();
            log.info("服务器已关闭");
            return;
        }

        // 创建离开房间响应
        LeaveRoomResponse leaveRoomResponse =
                new LeaveRoomResponse(new ArrayList<>(users.values()));
        leaveRoomResponse.setMessageId(request.getMessageId());
        // 广播给所有连接的客户端
        broadcastToAllClients(leaveRoomResponse);

        // 更新用户键盘状态
        userKeyBoards.remove(userId);
        // 更新用户游戏状态
        userPlayStates.remove(userId);
        // 广播更新游戏状态
        broadcastToAllClients(new PlayStatesUpdateResponse(userPlayStates));
    }

    /**
     * 开始游戏（广播）
     */
    private void startGame(StartGameRequest request) {
        int letterCount = request.getLetterCount();
        UUID userId = request.getUserId();
        log.info("房主{}开始游戏，字母数量：{}", userId, letterCount);

        // 检查房主是否是当前用户
        if (!userId.equals(hostUser.getId())) {
            log.warn("只有房主可以开始游戏");
            return;
        }

        // 广播给所有连接的客户端
        broadcastToAllClients(new StartGameResponse(letterCount));

        // 生成正确单词
        answer = WordUtils.getRandomWord(letterCount);

        // 初始化用户游戏状态
        for (UUID id : users.keySet()) {
            userPlayStates.put(id, new PlayState(0, 0));
            userKeyBoards.put(id, new LetterStatus[26]);
        }

        // 记录游戏开始时间
        startTime = System.currentTimeMillis();
    }

    private void sendToClient(UUID userId, BaseMessage message) {
        TcpJsonHelper client = clientConnections.get(userId);
        if (client == null) {
            log.warn("未找到用户 {} 的连接", userId);
            return;
        }

        try {
            client.sendMessage(message);
        } catch (IOException e) {
            log.error("发送消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 更新用户游戏状态列表（单播）
     */
    private void updatePlayStates(PlayStatesUpdateRequest request) {
        UUID userId = request.getUserId();
        log.info("用户 {} 请求更新游戏状态列表", userId);

        // 发送响应给请求的客户端
        PlayStatesUpdateResponse response = new PlayStatesUpdateResponse(userPlayStates);
        response.setMessageId(request.getMessageId());
        sendToClient(userId, response);
    }

    /**
     * 验证单词是否有效（单播）
     */
    private void validateWord(ValidateWordRequest request) {
        UUID userId = request.getUserId();
        log.info("用户 {} 验证单词: {}", userId, request.getWord());

        // 检查单词是否有效
        boolean isValid = WordUtils.isValidWord(request.getWord());

        // 创建验证单词响应，保留相同的messageId实现请求-响应匹配
        ValidateWordResponse response = new ValidateWordResponse(isValid);
        response.setMessageId(request.getMessageId());

        // 发送响应给请求的客户端
        sendToClient(userId, response);
    }

    /**
     * 检查单词（单播）
     */
    private void checkWord(CheckWordRequest request) {
        UUID userId = request.getUserId();
        String guessWord = request.getWord();
        log.info("检查单词: {} (来自用户 {})", guessWord, userId);

        // 检查单词
        List<LetterStatus> statusList = WordUtils.checkWord(guessWord, answer.getWord());
        boolean correct = statusList.stream()
                .allMatch(status -> status == LetterStatus.CORRECT);

        // 更新用户键盘状态
        updateUserKeyBoard(userId, guessWord, statusList);

        // 更新用户游戏状态
        PlayState playState = userPlayStates.get(userId);
        playState.setCorrectCount(getLetterStatusCount(userId, LetterStatus.CORRECT));
        playState.setWrongPositionCount(getLetterStatusCount(userId, LetterStatus.WRONG_POSITION));
        userPlayStates.put(userId, playState);

        // 发送更新游戏状态给所有客户端
        broadcastToAllClients(new PlayStatesUpdateResponse(userPlayStates));

        // 如果正确，记录游戏结束时间
        if (correct) {
            endTime = System.currentTimeMillis();
            log.info("用户 {} 猜对了单词: {}", userId, guessWord);
            User winner = users.get(userId);
            // 广播游戏结束消息
            broadcastToAllClients(new GameOverResponse(answer, winner, endTime - startTime));
        }

        // 发送响应给请求的客户端
        CheckWordResponse response = new CheckWordResponse(correct, statusList);
        response.setMessageId(request.getMessageId());
        sendToClient(userId, response);
    }

    /**
     * 根据单词状态更新用户键盘状态
     */
    private void updateUserKeyBoard(UUID userId, String guessWord, List<LetterStatus> statusList) {
        LetterStatus[] keyboard = userKeyBoards.get(userId);
        if (keyboard == null) {
            keyboard = new LetterStatus[26]; // 初始化键盘状态
            userKeyBoards.put(userId, keyboard);
        }

        String lowerGuessWord = guessWord.toLowerCase();
        for (int i = 0; i < lowerGuessWord.length(); i++) {
            char letter = lowerGuessWord.charAt(i);
            int index = letter - 'a';
            if (index >= 0 && index < 26) {
                LetterStatus oldStatus = keyboard[index];
                LetterStatus newStatus = statusList.get(i);
                // 需要更新的情况：
                // 1. 新状态是错误位置且旧状态不是正确
                // 2. 新状态是正确
                // 3. 旧状态为空
                if (newStatus == LetterStatus.WRONG_POSITION && oldStatus != LetterStatus.CORRECT
                        || newStatus == LetterStatus.CORRECT
                        || oldStatus == null) {
                    keyboard[index] = newStatus;
                }
            }
        }
    }

    /**
     * 获取用户的字母状态计数
     */
    private int getLetterStatusCount(UUID userID, LetterStatus letterStatus) {
        LetterStatus[] keyboard = userKeyBoards.get(userID);
        if (keyboard == null) {
            return 0;
        }

        int count = 0;
        for (LetterStatus status : keyboard) {
            if (status == letterStatus) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取答案（单播）
     */
    private void getAnswer(GetAnswerRequest request) {
        UUID userId = request.getUserId();
        log.info("收到获取答案请求 userId={}", userId);

        // 创建答案响应，保留相同的messageId实现请求-响应匹配
        GetAnswerResponse response = new GetAnswerResponse(answer);
        response.setMessageId(request.getMessageId());

        sendToClient(userId, response);
    }
}
