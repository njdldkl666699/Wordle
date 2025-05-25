package io.njdldkl.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Word;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;

@Slf4j
public class WordUtils {

    // 存储单词列表的映射，键为单词长度，值为单词列表，用于生成随机单词
    private static final Map<String, List<Word>> WORD_LIST_MAP;

    // 存储单词的映射，键为单词，值为单词对象，用于验证单词
    private static final Map<String, Word> WORD_MAP;

    static {
        try (InputStream inputStream = WordUtils.class.getResourceAsStream("/wordlist.json")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("资源未找到: /wordlist.json");
            }
            String jsonString = new String(inputStream.readAllBytes());
            WORD_LIST_MAP = JSON.parseObject(jsonString, new TypeReference<>() {
            });

            WORD_MAP = new HashMap<>();
            for (List<Word> words : WORD_LIST_MAP.values()) {
                for (Word word : words) {
                    WORD_MAP.put(word.getWord(), word);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加载单词列表失败", e);
        }
    }

    /**
     * 获取指定长度的随机单词
     *
     * @param length 单词长度
     * @return 随机单词
     */
    public static Word getRandomWord(int length) {
        List<Word> words = WORD_LIST_MAP.get(String.valueOf(length));
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException(length + "长度的单词未找到");
        }
        int randomIndex = (int) (Math.random() * words.size());
        return words.get(randomIndex);
    }

    /**
     * 检查单词是否有效
     *
     * @param word 要检查的单词
     * @return true 如果单词有效，false 否则
     */
    public static boolean isValidWord(String word) {
        // 检查单词是否存在于WORD_MAP中
        // 转换为小写，因为WORD_MAP中的单词都是小写的
        String lowerCase = word.toLowerCase();
        return WORD_MAP.containsKey(lowerCase);
    }

    /**
     * <p>检查单词</p>
     * 例如：guessWord为apael，answer为apple<br>
     * 返回的状态列表为<br>
     * [CORRECT, CORRECT, NOT_IN_WORD, WRONG_POSITION, WRONG_POSITION]
     *
     * @param guessWord 猜测单词
     * @param answer    正确单词
     * @return 单词状态列表
     */
    public static List<WordStatus> checkWord(String guessWord, String answer) {
        // 检查单词长度是否一致
        if (guessWord.length() != answer.length()) {
            throw new IllegalArgumentException("猜测单词和答案长度不一致");
        }

        // 将猜测的单词和答案都转为大写，避免大小写问题
        guessWord = guessWord.toUpperCase();
        answer = answer.toUpperCase();
        int length = answer.length();

        // 初始化状态列表，默认所有字母都不在单词中
        List<WordStatus> statusList = new ArrayList<>(
                Collections.nCopies(guessWord.length(), WordStatus.ABSENT));

        // 用于统计字母出现的次数
        int[] letterCount = new int[26];
        // 统计答案字母并先处理完全匹配的情况
        answer.chars().forEach(c -> letterCount[c - 'A']++);

        // 第一遍：标记完全正确的字母
        for (int i = 0; i < length; i++) {
            if (guessWord.charAt(i) == answer.charAt(i)) {
                statusList.set(i, WordStatus.CORRECT);
                letterCount[guessWord.charAt(i) - 'A']--;
            }
        }

        // 第二遍：处理位置错误的字母
        for (int i = 0; i < guessWord.length(); i++) {
            if (statusList.get(i) != WordStatus.CORRECT) {
                char c = guessWord.charAt(i);
                if (letterCount[c - 'A'] > 0) {
                    statusList.set(i, WordStatus.WRONG_POSITION);
                    letterCount[c - 'A']--;
                }
            }
        }

        log.debug("猜测单词: {}, 答案: {}, 状态: {}", guessWord, answer, statusList);
        return statusList;
    }
}
