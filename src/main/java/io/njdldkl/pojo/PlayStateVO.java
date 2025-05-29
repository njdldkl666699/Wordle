package io.njdldkl.pojo;

import lombok.Builder;
import lombok.Data;

import javax.swing.*;

@Data
@Builder
public class PlayStateVO {

    // 昵称
    private String name;
    // 头像
    private ImageIcon avatar;
    // 字母正确且位置正确的数量
    private int correctCount;
    // 字母正确但位置错误的数量
    private int wrongPositionCount;
}
