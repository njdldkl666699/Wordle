package io.njdldkl.service;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;

import java.util.List;

/**
 * 游戏服务接口，处理游戏判断逻辑和数据存储传输
 */
public interface PlayService {

    /**
     * 注册用户
     *
     * @param user 用户对象
     */
    void registerUser(User user);

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
     * @return 正确与否，单词状态列表
     */
    Pair<Boolean,List<WordStatus>> checkWord(String guessWord);

    /**
     * 获取正确单词
     *
     * @return 正确单词
     */
    Word getAnswer();

    /**
     * 判断是否失败
     * @return true 失败，false 否则
     */
    boolean isFailed();
}
