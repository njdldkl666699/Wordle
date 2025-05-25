package io.njdldkl.pojo.request;

import lombok.Data;

import java.util.UUID;

@Data
public class LeaveRoomRequest extends BaseRequest {

    public LeaveRoomRequest(UUID userId) {
        setUserId(userId);
    }
}
