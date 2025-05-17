package io.njdldkl.service.impl;

import io.njdldkl.enumerable.WordStatus;
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
    public void startGame(int wordLength) {
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
        if (currentGuessCount >= maxGuessCount) {
            return false;
        }
        currentGuessCount++;
        return WordUtils.isValidWord(word);
    }

    /**
     * 检查单词
     */
    @Override
    public List<WordStatus> checkWord(String guessWord) {
        return WordUtils.checkWord(guessWord, answer.getWord());
    }

    /**
     * <p>获取正确单词</p>
     * 单人游戏下，答案直接存储在service中
     */
    @Override
    public Word getAnswer() {
        return answer;
    }
}
