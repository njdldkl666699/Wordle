package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseMessage {

    public BaseMessage() {
        // 根据实际类的类型来设置请求类型
        this.type = this.getClass().getSimpleName();
    }

    // 请求类型
    private String type;
}
