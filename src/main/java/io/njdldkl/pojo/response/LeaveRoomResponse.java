package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaveRoomResponse extends BaseMessage {

    // 离开房间的用户id
    private UUID userId;
}
