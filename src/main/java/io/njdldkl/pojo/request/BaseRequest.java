package io.njdldkl.pojo.request;

import io.njdldkl.pojo.BaseMessage;
import lombok.Data;

import java.util.UUID;

@Data
public class BaseRequest extends BaseMessage {

    public BaseRequest(){
        setMessageId(UUID.randomUUID());
    }

    // 用户id
    private UUID userId;
}
