package io.njdldkl.service.impl;

import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.PlayService;
import io.njdldkl.util.WordUtils;

import java.util.List;

public class SinglePlayService implements PlayService {

    // 正确单词
    private Word answer;

    // 当前猜测次数
    private int currentGuessCount;

    // 最大猜测次数
    private int maxGuessCount;

    /**
     * <p>开启新的一局游戏</p>
     * 单人游戏下，直接重置游戏状态
     */
    @Override
    public void startGame(int wordLength, User user) {
        // 单人模式，user参数暂时不使用
        answer = WordUtils.getRandomWord(wordLength);
        currentGuessCount = 0;
        maxGuessCount = wordLength;
    }

    /**
     * <p>检查单词是否有效</p>
     * 每检查一次，猜测次数加1，猜测次数超过最大次数，返回false
     */
    @Override
    public boolean isValidWord(String word) {
        return WordUtils.isValidWord(word);
    }

    /**
     * 检查单词
     */
    @Override
    public Pair<Boolean, List<WordStatus>> checkWord(String guessWord) {
        currentGuessCount++;
        List<WordStatus> statusList = WordUtils.checkWord(guessWord, answer.getWord());
        boolean correct = statusList.stream()
                .allMatch(status -> status == WordStatus.CORRECT);
        // NOTE: 多人对战下，将结果和正确玩家存储在service中
        // 单人游戏下，直接返回结果
        return new Pair<>(correct, statusList);
    }

    /**
     * <p>获取正确单词</p>
     * 单人游戏下，答案直接存储在service中
     */
    @Override
    public Word getAnswer() {
        return answer;
    }

    /**
     * <p>判断是否失败</p>
     * 单人游戏下，失败条件为猜测次数超过最大次数
     */
    @Override
    public boolean isFailed() {
        return currentGuessCount >= maxGuessCount;
    }
}
