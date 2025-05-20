package io.njdldkl.net;

import com.alibaba.fastjson2.JSONObject;

/**
 * 异步消息处理器接口
 */
public interface MessageHandler {

    /**
     * 处理接收到的JSON消息
     */
    void handleMessage(JSONObject json);

    /**
     * 处理异常
     */
    void onError(Exception e);
}
