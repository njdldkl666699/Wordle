package io.njdldkl.net;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.njdldkl.pojo.BaseMessage;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 在TCP网络通信上发送和接收JSON数据的帮助类
 */
public class TcpJsonHelper {

    // 不同的json消息之间的分隔符
    private static final byte[] DELIMITER = "\n".getBytes(StandardCharsets.UTF_8);

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    private final MessageHandler messageHandler;
    private Thread receiverThread;

    public TcpJsonHelper(Socket socket, MessageHandler handler) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
        this.messageHandler = handler;
    }

    /**
     * 启动接收线程
     */
    public void startReceiver() {
        receiverThread = new Thread(this::receiveLoop);
        receiverThread.start();
    }

    /**
     * 停止接收线程
     */
    public void stopReceiver() {
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
    }

    /**
     * 发送JSON数据
     */
    public void sendMessage(BaseMessage message) throws IOException {
        // 将对象转换为JSON对象
        JSONObject json = JSONObject.from(message);
        byte[] jsonBytes = JSON.toJSONBytes(json);
        output.write(jsonBytes);
        output.write(DELIMITER);
        output.flush();
    }

    public void close() {
        stopReceiver();
        IOUtils.closeQuietly(input, output, socket);
    }

    private void receiveLoop() {
        try {
            ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while (!Thread.currentThread().isInterrupted() &&
                    (bytesRead = input.read(buffer)) != -1) {
                messageBuffer.write(buffer, 0, bytesRead);

                byte[] receivedData = messageBuffer.toByteArray();
                int delimiterIndex = indexOf(receivedData, DELIMITER);

                // 如果找到分隔符，说明有完整的消息
                while (delimiterIndex != -1) {
                    byte[] messageBytes = new byte[delimiterIndex];
                    System.arraycopy(receivedData, 0, messageBytes, 0, delimiterIndex);

                    try {
                        // 解析JSON消息并处理
                        JSONObject jsonObject = JSON.parseObject(messageBytes);
                        // 获取消息类型
                        String type = jsonObject.getString("type");
                        messageHandler.receiveMessage(jsonObject, type);
                    } catch (Exception e) {
                        messageHandler.onError(e);
                    }

                    // 处理完消息后，将分隔符后的数据移回缓冲区
                    byte[] remaining = IOUtils.byteArray(receivedData.length - delimiterIndex - DELIMITER.length);
                    System.arraycopy(receivedData, delimiterIndex + DELIMITER.length,
                            remaining, 0, remaining.length);
                    receivedData = remaining;
                    messageBuffer.reset();
                    messageBuffer.write(remaining);

                    // 继续查找下一个分隔符
                    delimiterIndex = indexOf(receivedData, DELIMITER);
                }
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                messageHandler.onError(e);
            }
        }
    }

    /**
     * 查找字节数组中是否包含目标字节数组
     */
    private static int indexOf(byte[] array, byte[] target) {
        if (target.length == 0) return 0;

        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * 异步消息处理器接口
     */
    public static interface MessageHandler {

        /**
         * 处理接收到的JSON消息
         */
        void receiveMessage(JSONObject jsonObject, String type);

        /**
         * 处理异常
         */
        void onError(Exception e);
    }
}
