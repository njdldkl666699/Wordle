import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Word;
import io.njdldkl.util.WordUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class WordUtilsTest {

    @Test
    public void testGetRandomWord() {
        // 测试获取长度为5的随机单词
        Word randomWord = WordUtils.getRandomWord(5);
        assertNotNull(randomWord);
        assertEquals(5, randomWord.getWord().length());
        log.debug("获取的随机单词: {}", randomWord);

        // 测试获取长度为6的随机单词
        randomWord = WordUtils.getRandomWord(6);
        assertNotNull(randomWord);
        assertEquals(6, randomWord.getWord().length());
        log.debug("获取的随机单词: {}", randomWord);

        // 测试获取不存在的长度的单词
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            WordUtils.getRandomWord(12);
        });
        assertEquals("12长度的单词未找到", exception.getMessage());
    }

    @Test
    public void testIsValidWord() {
        // 测试有效单词
        String validWord = "apple";
        assertTrue(WordUtils.isValidWord(validWord));

        // 测试无效单词
        String invalidWord = "invalidword";
        assertFalse(WordUtils.isValidWord(invalidWord));

        // 测试空字符串
        assertFalse(WordUtils.isValidWord(""));

        // 测试null
        assertFalse(WordUtils.isValidWord(null));
    }

    @ParameterizedTest
    @MethodSource
    public void testCheckWord(String guessWord, String answer, List<WordStatus> expected) {
        // 执行测试
        List<WordStatus> result = WordUtils.checkWord(guessWord, answer);

        // 验证结果
        assertEquals(expected, result, String.format("猜测单词: %s, 答案: %s", guessWord, answer));
    }

    private static Stream<Arguments> testCheckWord() {
        return Stream.of(
                // 完全匹配
                Arguments.of("apple", "apple",
                        List.of(WordStatus.CORRECT, WordStatus.CORRECT, WordStatus.CORRECT,
                                WordStatus.CORRECT, WordStatus.CORRECT)),

                // 完全不匹配
                Arguments.of("abcde", "fghij",
                        List.of(WordStatus.ABSENT, WordStatus.ABSENT,
                                WordStatus.ABSENT, WordStatus.ABSENT,
                                WordStatus.ABSENT)),

                // 部分匹配 - 示例中的情况
                Arguments.of("apael", "apple",
                        List.of(WordStatus.CORRECT, WordStatus.CORRECT, WordStatus.ABSENT,
                                WordStatus.WRONG_POSITION, WordStatus.WRONG_POSITION)),

                // 重复字母测试
                Arguments.of("aabbc", "abcba",
                        List.of(WordStatus.CORRECT, WordStatus.WRONG_POSITION,
                                WordStatus.WRONG_POSITION, WordStatus.CORRECT,
                                WordStatus.WRONG_POSITION)),

                // 大小写混合测试
                Arguments.of("ApPlE", "aPpLe",
                        List.of(WordStatus.CORRECT, WordStatus.CORRECT, WordStatus.CORRECT,
                                WordStatus.CORRECT, WordStatus.CORRECT))
        );
    }
}
