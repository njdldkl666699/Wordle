package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameOverResponse extends BaseMessage {

    // 正确单词
    private Word answer;

    // 获胜用户id
    private User winner;

    // 获胜玩家游戏时长
    private long winnerDuration;
}
