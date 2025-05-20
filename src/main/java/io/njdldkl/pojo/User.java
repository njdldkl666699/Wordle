package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class User {

    // 用户ID
    private String name;
    // 用户头像
    private ImageIcon avatar;

}
