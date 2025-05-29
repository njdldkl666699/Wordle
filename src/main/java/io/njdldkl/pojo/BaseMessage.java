package io.njdldkl.pojo;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseMessage {

    public BaseMessage() {
        // 根据实际类的类型来设置请求类型
        this.type = this.getClass().getSimpleName();
    }

    // 消息类型
    private String type;
    // 消息id，成对的请求和响应用一个id
    private UUID messageId;
}
