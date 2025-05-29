package io.njdldkl.enumerable;

/**
 * 字母状态
 */
public enum LetterStatus {

    // 字母正确且位置正确
    CORRECT,
    // 字母正确但位置错误
    WRONG_POSITION,
    // 字母不在单词中
    ABSENT
}
