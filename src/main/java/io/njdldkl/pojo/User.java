package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class User {

    // 用户ID
    private UUID id;
    // 用户名
    private String name;
    // 用户头像
    private ImageIcon avatar;


    // TODO 头像序列化为Base64字符串

}
