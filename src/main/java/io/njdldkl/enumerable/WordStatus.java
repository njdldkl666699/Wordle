package io.njdldkl.enumerable;

/**
 * 单词状态
 */
public enum WordStatus {

    // 字母正确且位置正确
    CORRECT,
    // 字母正确但位置错误
    WRONG_POSITION,
    // 字母不在单词中
    ABSENT
}
