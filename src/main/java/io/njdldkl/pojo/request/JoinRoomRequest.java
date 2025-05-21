package io.njdldkl.pojo.request;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import lombok.*;

@Data
@AllArgsConstructor
public class JoinRoomRequest extends BaseMessage {

    // 用户
    private User user;
    // 房间ID
    private String roomId;
}

