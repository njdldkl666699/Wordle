package io.njdldkl.pojo.response;

import io.njdldkl.pojo.BaseMessage;
import io.njdldkl.pojo.Word;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAnswerResponse extends BaseMessage {

    private Word answer;
}
