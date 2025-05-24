package io.njdldkl.pojo.request;

import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class StartGameRequest extends BaseMessage {

    // 房主id
    private UUID userId;

    // 字母数量
    private int letterCount;
}
