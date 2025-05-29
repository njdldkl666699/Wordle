package io.njdldkl.pojo.request;

import io.njdldkl.pojo.User;
import lombok.Data;

@Data
public class JoinRoomRequest extends BaseRequest {

    public JoinRoomRequest(User user, String roomId) {
        this.user = user;
        this.roomId = roomId;
        setUserId(user.getId());
    }

    // 用户
    private User user;
    // 房间ID
    private String roomId;
}

