package io.njdldkl.pojo.request;

import lombok.Data;

import java.util.UUID;

@Data
public class GetAnswerRequest extends BaseRequest {

    public GetAnswerRequest(UUID userId) {
        setUserId(userId);
    }
}
