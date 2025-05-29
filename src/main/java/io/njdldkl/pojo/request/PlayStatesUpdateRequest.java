package io.njdldkl.pojo.request;

import java.util.UUID;

public class PlayStatesUpdateRequest extends BaseRequest {

    public PlayStatesUpdateRequest(UUID userId) {
        setUserId(userId);
    }
}
