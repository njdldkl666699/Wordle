package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.PlayState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayStatesUpdatedResponse extends BaseMessage {

    // 用户游戏状态列表
    private Map<UUID, PlayState> playStates;
}
