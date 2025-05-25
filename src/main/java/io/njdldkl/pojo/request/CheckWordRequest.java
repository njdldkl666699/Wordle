package io.njdldkl.pojo.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CheckWordRequest extends BaseRequest{

    public CheckWordRequest(UUID userId, String word) {
        setUserId(userId);
        this.word = word;
    }

    // 猜测的单词
    private String word;
}
