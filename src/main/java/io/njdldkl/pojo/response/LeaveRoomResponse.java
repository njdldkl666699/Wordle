package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeaveRoomResponse extends BaseMessage {

    // 用户id列表
    private List<User> userList;
}
