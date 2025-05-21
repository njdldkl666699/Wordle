package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class JoinRoomResponse extends BaseMessage {

    // 所有用户（除了房主）
    private List<User> users;
    // 房主
    private User host;
}
