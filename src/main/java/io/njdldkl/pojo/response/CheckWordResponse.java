package io.njdldkl.pojo.response;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.BaseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CheckWordResponse extends BaseMessage {

    private boolean correct;

    private List<WordStatus> statusList;
}
