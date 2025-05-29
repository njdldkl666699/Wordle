package io.njdldkl.pojo;

import com.alibaba.fastjson2.annotation.JSONField;
import io.njdldkl.serial.ImageIconDeserializer;
import io.njdldkl.serial.ImageIconSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // 用户ID
    private UUID id;
    // 用户名
    private String name;
    // 用户头像
    // 使用自定义序列化和反序列化器处理ImageIcon
    @JSONField(serializeUsing = ImageIconSerializer.class,
            deserializeUsing = ImageIconDeserializer.class)
    private ImageIcon avatar;

}
