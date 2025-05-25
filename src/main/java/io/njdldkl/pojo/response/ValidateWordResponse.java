package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateWordResponse extends BaseMessage {

    // 单词是否有效
    private boolean valid;
}
