package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayState {

    // 字母正确且位置正确的数量
    private int correctCount;
    // 字母正确但位置错误的数量
    private int wrongPositionCount;
}
