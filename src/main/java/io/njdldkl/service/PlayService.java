package io.njdldkl.service;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Word;

import java.util.List;

/**
 * 游戏服务接口，处理游戏判断逻辑和数据存储传输
 */
public interface PlayService {

    /**
     * 开启新的一局游戏
     */
    void startGame(int wordLength);

    /**
     * 检查单词是否有效
     *
     * @param word 要检查的单词
     * @return true 如果单词有效，false 否则
     */
    boolean isValidWord(String word);

    /**
     * 检查单词
     *
     * @param guessWord 猜测单词
     * @return 单词状态列表
     */
    List<WordStatus> checkWord(String guessWord);

    /**
     * 获取正确单词
     *
     * @return 正确单词
     */
    Word getAnswer();
}
