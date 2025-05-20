package io.njdldkl.pojo.request;

import io.njdldkl.pojo.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomRequest {

    // 请求类型
    private String type;
    // 用户
    private User user;
    // 房间ID
    private String roomId;
}
