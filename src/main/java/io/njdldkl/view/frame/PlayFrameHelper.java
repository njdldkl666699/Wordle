package io.njdldkl.view.frame;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.DimensionConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.enumerable.WordStatus;
import io.njdldkl.pojo.Pair;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.PlayService;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.component.RoundedLetterPanel;
import io.njdldkl.view.dialog.AutoCloseDialog;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏窗口工具类，存放单人游戏和多人对战的公共UI逻辑
 */
@Slf4j
@Builder
public class PlayFrameHelper {

    private static final Font LETTER_FONT = new Font("Arial", Font.BOLD, 36);

    private final JFrame frame;

    private final RoundedButton homeButton;
    private final RoundedButton giveUpButton;

    private final JScrollPane guessScrollPane;
    private final JPanel guessPane;
    private final KeyboardPanel keyboardPane;

    private PlayService playService;

    private User user;

    // 总行数
    private int totalRow;
    // 总列数
    private int totalCol;
    // 当前行数
    private int currentRow;
    // 当前列数
    private int currentCol;

    /**
     * 重置未传入的字段
     *
     * @param letterCount 字母数量
     */
    private void resetFields(int letterCount) {
        // 更新总行数、总列数
        this.totalRow = letterCount + 1;
        this.totalCol = letterCount;
        // 重置当前行列
        this.currentRow = 0;
        this.currentCol = 0;
        // 重置键盘状态
        keyboardPane.resetKeyboard();
    }

    /**
     * 初始化UI
     */
    public void initUI() {
        guessScrollPane.setViewportView(guessPane);
        guessScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        keyboardPane.resetKeyboard();
    }

    /**
     * 设置监听器
     */
    public void setListeners() {
        // 返回主菜单按钮
        homeButton.addActionListener(e -> WindowManager.getInstance().showMenuFrame());

        // 认输按钮
        giveUpButton.addActionListener(e -> WindowManager.getInstance()
                .showGameOverDialog("游戏失败！", playService.getAnswer(), frame));

        // 初始化键盘
        keyboardPane.setKeyListener(e -> {
            JButton button = (JButton) e.getSource();
            String key = button.getName();
            handleKeyPress(key);
        });

        // 设置本窗口处理键盘事件
        JRootPane rootPane = frame.getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        // 为回车键添加动作
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "handleEnter");
        actionMap.put("handleEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleKeyPress("ENTER");
            }
        });

        // 为退格键添加动作
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "handleDelete");
        actionMap.put("handleDelete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleKeyPress("DEL");
            }
        });

        // 为字母键添加动作（A-Z）
        for (char c = 'A'; c <= 'Z'; c++) {
            final String letter = String.valueOf(c);
            inputMap.put(KeyStroke.getKeyStroke(c), "handle" + letter);
            actionMap.put("handle" + letter, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleKeyPress(letter);
                }
            });
        }

        // 为字母键添加动作（a-z）
        for (char c = 'a'; c <= 'z'; c++) {
            final String letter = String.valueOf(c);
            inputMap.put(KeyStroke.getKeyStroke(c), "handle" + letter);
            actionMap.put("handle" + letter, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleKeyPress(letter.toUpperCase());
                }
            });
        }
    }

    /**
     * 根据字母数量更新猜单词面板
     */
    public void updateGuessPane(int letterCount, User user) {
        // 重置游戏状态
        resetFields(letterCount);
        if (user != null) {
            this.user = user;
            playService.registerUser(user);
        }
        playService.startGame(letterCount);

        // 清空面板
        guessPane.removeAll();
        // 使用 GridLayout 来替代 BoxLayout
        guessPane.setLayout(new GridLayout(totalRow, 1, 0, 10));

        // totalRow行
        for (int i = 0; i < totalRow; i++) {
            // 为每一行创建独立的面板
            JPanel letterRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            letterRow.setBackground(Color.WHITE);

            // totalCol 列
            for (int j = 0; j < totalCol; j++) {
                RoundedLetterPanel letterPanel = new RoundedLetterPanel(
                        DimensionConstant.LETTER_PANEL, LETTER_FONT, ColorConstant.BLANK,
                        IntegerConstant.SHARP_RADIUS, ColorConstant.GRAY, 3);
                letterPanel.setPreferredSize(new Dimension(60, 60));
                letterRow.add(letterPanel);
            }

            // 将行面板添加到主面板
            guessPane.add(letterRow);
        }

        guessPane.revalidate();
        guessPane.repaint();
        guessScrollPane.revalidate();
        guessScrollPane.repaint();
    }

    /**
     * 处理按键事件
     */
    private void handleKeyPress(String key) {
        switch (key) {
            case "ENTER" -> submitGuess();
            case "DEL" -> deleteLastChar();
            default -> addCharToCurrentGuess(key);
        }
    }

    /**
     * 提交猜测
     */
    private void submitGuess() {
        log.info("提交猜测");
        // 检查当前列索引是否到达总列数
        if (currentCol < totalCol) {
            log.info("单词未猜测完整，无法提交");
            // 弹出提示框，提示用户单词未猜测完整
            AutoCloseDialog dialog = new AutoCloseDialog(frame);
            dialog.setText("单词太短");
            dialog.setVisible(true);
            return;
        }

        // 获取当前行的单词
        String guessWord = getCurrentWord();
        // 提交猜测并检验是否合法
        boolean valid = playService.isValidWord(guessWord);
        if (!valid) {
            log.info("单词不合法");
            AutoCloseDialog dialog = new AutoCloseDialog(frame);
            dialog.setText("单词未找到");
            dialog.setVisible(true);
            return;
        }

        // 检查是否猜测正确
        Pair<Boolean, List<WordStatus>> pairResult = playService.checkWord(guessWord);
        boolean correct = pairResult.getFirst();
        List<WordStatus> wordStatusList = pairResult.getSecond();

        // 构造字母和状态的列表
        List<Pair<String, WordStatus>> pairList = new ArrayList<>();
        for (int i = 0; i < wordStatusList.size(); i++) {
            String letter = guessWord.substring(i, i + 1);
            WordStatus wordStatus = wordStatusList.get(i);
            pairList.add(new Pair<>(letter, wordStatus));
        }
        // 更新当前行的字母面板颜色
        updateLetterPanelByStatusList(pairList);
        // 更新键盘状态
        updateKeyboardByStatusList(pairList);
        // 滚动到当前行
        JScrollBar scrollBar = guessScrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());

        // 更新UI
        guessPane.revalidate();
        guessPane.repaint();
        guessScrollPane.revalidate();
        guessScrollPane.repaint();
        keyboardPane.revalidate();
        keyboardPane.repaint();

        // 如果全部正确，弹出对话框
        if (correct) {
            log.info("猜测正确，游戏胜利");
            WindowManager.getInstance()
                    .showGameOverDialog("游戏胜利！", playService.getAnswer(), frame);
            return;
        }

        // 更新当前行列索引
        currentRow++;
        // 判断是否失败
        // 单人模式下，失败条件为猜测次数超过最大次数，正常情况下不起作用
        // 多人模式下，最先猜测正确的玩家获胜，其余玩家失败
        if (playService.isFailed()) {
            Word answer = playService.getAnswer();
            log.info("游戏失败，正确单词为: {}", answer.getWord());
            WindowManager.getInstance()
                    .showGameOverDialog("游戏失败！", answer, frame);
            return;
        }
        currentCol = 0;
    }

    /**
     * 获取当前行的单词
     */
    private String getCurrentWord() {
        JPanel letterRow = (JPanel) guessPane.getComponent(currentRow);
        StringBuilder guessWordSB = new StringBuilder();
        for (int i = 0; i < totalCol; i++) {
            RoundedLetterPanel letterPanel = (RoundedLetterPanel) letterRow.getComponent(i);
            String letter = letterPanel.getLetter();
            guessWordSB.append(letter);
        }
        return guessWordSB.toString();
    }

    /**
     * 根据单词状态映射更新字母面板的颜色
     */
    private void updateLetterPanelByStatusList(List<Pair<String, WordStatus>> pairList) {
        JPanel letterRow = (JPanel) guessPane.getComponent(currentRow);
        for (int i = 0; i < totalCol; i++) {
            RoundedLetterPanel letterPanel = (RoundedLetterPanel) letterRow.getComponent(i);
            String letter = pairList.get(i).getFirst();
            WordStatus wordStatus = pairList.get(i).getSecond();

            // 设置无边框
            letterPanel.setHasBorder(false);
            // 设置背景颜色
            Color bgColor = switch (wordStatus) {
                case CORRECT -> ColorConstant.GREEN;
                case WRONG_POSITION -> ColorConstant.YELLOW;
                case ABSENT -> ColorConstant.GRAY;
            };
            letterPanel.setBackground(bgColor);
        }
    }

    /**
     * 据单词状态映射更新键盘的颜色
     */
    private void updateKeyboardByStatusList(List<Pair<String, WordStatus>> pairList) {
        for (Pair<String, WordStatus> pair : pairList) {
            String letter = pair.getFirst();
            WordStatus wordStatus = pair.getSecond();
            switch (wordStatus) {
                case CORRECT -> keyboardPane.setCorrect(letter);
                case WRONG_POSITION -> keyboardPane.setWrongPosition(letter);
                case ABSENT -> keyboardPane.setAbsent(letter);
            }
        }
    }

    /**
     * 删除最后一个字母
     */
    private void deleteLastChar() {
        log.info("删除最后一个字母");

        // 1. 检查当前列索引
        if (currentCol <= 0) {
            log.info("当前列索引已为0，无法删除字母");
            // 也可以给用户提示
            return;
        }
        // 2. 当前列-1
        currentCol--;
        // 3. 获取当前行列的字母面板，设置字母为空
        RoundedLetterPanel letterPanel = getCurrentLetterPanel();
        letterPanel.setLetter("");

        // 4. 滚动到当前行，更新UI
        guessPane.revalidate();
        guessPane.repaint();
    }

    /**
     * 添加字母到当前猜测
     */
    private void addCharToCurrentGuess(String key) {
        log.debug("添加字母: {}", key);

        // 1. 检查当前行列是否已满
        if (currentRow >= totalRow || currentCol >= totalCol) {
            log.info("当前行列已满，无法添加字母");
            // 也可以给用户提示
            return;
        }

        // 2. 获取当前行列的字母面板
        RoundedLetterPanel letterPanel = getCurrentLetterPanel();
        // 3. 添加大写字母到当前行
        letterPanel.setLetter(key.toUpperCase());
        // 4. 更新当前行列
        currentCol++;

        // 5. 更新UI
        guessPane.revalidate();
        guessPane.repaint();
    }

    /**
     * <p>获取当前行列的字母面板对象引用</p>
     * 当前行列指向最后一个<b>有字母的下一个字母面板</b>
     */
    private RoundedLetterPanel getCurrentLetterPanel() {
        JPanel currentRowPanel = (JPanel) guessPane.getComponent(currentRow);
        return (RoundedLetterPanel) currentRowPanel.getComponent(currentCol);
    }
}
