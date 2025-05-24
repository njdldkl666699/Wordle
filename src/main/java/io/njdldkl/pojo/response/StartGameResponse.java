package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartGameResponse extends BaseMessage {

    // 字母数量
    private int letterCount;
}
