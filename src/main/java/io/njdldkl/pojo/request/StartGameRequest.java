package io.njdldkl.pojo.request;

import lombok.Data;

import java.util.UUID;

@Data
public class StartGameRequest extends BaseRequest {

    public StartGameRequest(UUID userId, int letterCount) {
        setUserId(userId);
        this.letterCount = letterCount;
    }

    // 字母数量
    private int letterCount;
}
