package io.njdldkl.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 单词实体类
 */
@Data
@AllArgsConstructor
public class Word {

    private String word;

    private String meaning;
}
