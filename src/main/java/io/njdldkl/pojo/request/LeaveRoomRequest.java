package io.njdldkl.pojo.request;

import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaveRoomRequest extends BaseMessage {

    // 用户id
    private UUID userId;
}
