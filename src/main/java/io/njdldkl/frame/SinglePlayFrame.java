package io.njdldkl.frame;

import io.njdldkl.WindowManager;
import io.njdldkl.component.KeyboardPanel;
import io.njdldkl.component.RoundedButton;
import io.njdldkl.component.RoundedLetterPanel;
import io.njdldkl.component.RoundedRadioButton;
import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

@Slf4j
public class SinglePlayFrame extends BaseFrame {

    private JPanel contentPane;

    private RoundedButton settingsButton;
    private RoundedButton homeButton;

    private JScrollPane guessScrollPane;
    private JPanel guessPane;
    private KeyboardPanel keyboardPane;

    private JPanel settingsPane;
    private RoundedRadioButton letter4RadioButton;
    private RoundedRadioButton letter5RadioButton;
    private RoundedRadioButton letter6RadioButton;
    private RoundedRadioButton letter7RadioButton;
    private RoundedRadioButton letter8RadioButton;
    private RoundedRadioButton letter9RadioButton;
    private RoundedRadioButton letter10RadioButton;
    private RoundedRadioButton letter11RadioButton;
    private ButtonGroup letterButtonGroup;

    // 字母数量，默认数量为5
    private int letterCount = 5;

    public SinglePlayFrame() {
        setContentPane(contentPane);

        guessScrollPane.setViewportView(guessPane);
        guessScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // 默认字母数量为5
        letterButtonGroup.setSelected(letter5RadioButton.getModel(), true);
        updateGuessPane();
        keyboardPane.resetKeyboard();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        // 初始化键盘
        keyboardPane.setKeyListener(e -> {
            JButton button = (JButton) e.getSource();
            String key = button.getName();
            handleKeyPress(key);
        });

        // 设置本窗口处理键盘事件
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    handleKeyPress("ENTER");
                } else if (keyCode == KeyEvent.VK_DELETE) {
                    handleKeyPress("DEL");
                } else {
                    char keyChar = e.getKeyChar();
                    if (Character.isLetter(keyChar)) {
                        handleKeyPress(String.valueOf(keyChar).toUpperCase());
                    }
                }
            }
        });

        // 返回主菜单按钮
        homeButton.addActionListener(e -> WindowManager.getInstance().showMenuFrame());

        // 设置面板
        setupLetterButtonGroupListener();
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));
    }

    /**
     * 为所有字母数量按钮添加监听器
     */
    private void setupLetterButtonGroupListener() {
        // 为按钮组中的所有按钮添加ActionListener
        Enumeration<AbstractButton> buttons = letterButtonGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            button.addActionListener(e -> {
                // 当任何按钮被点击时，获取选中的值
                if (button.isSelected()) {
                    int newLetterCount = Integer.parseInt(button.getActionCommand());
                    log.info("字母数量已更改为: {}", newLetterCount);

                    // 立即更新字母数量，并更新面板
                    letterCount = newLetterCount;
                    updateGuessPane();
                    keyboardPane.resetKeyboard();

                    // 关闭设置面板
                    settingsPane.setVisible(false);
                }
            });
        }
    }

    /**
     * 根据字母数量更新猜单词面板
     */
    private void updateGuessPane() {
        guessPane.removeAll();

        // 使用 GridLayout 来替代 BoxLayout
        guessPane.setLayout(new GridLayout(letterCount + 1, 1, 0, 10));

        // (字母行数 + 1)行
        for (int i = 0; i < letterCount + 1; i++) {
            // 为每一行创建独立的面板
            JPanel letterRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            letterRow.setBackground(Color.WHITE);

            // 字母行数 列
            for (int j = 0; j < letterCount; j++) {
                RoundedLetterPanel letterPanel = new RoundedLetterPanel(
                        new Dimension(50, 50),
                        ColorConstant.BLANK,
                        IntegerConstant.SHARP_RADIUS,
                        ColorConstant.GRAY,
                        3);
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

    }

    /**
     * 删除最后一个字母
     */
    private void deleteLastChar() {

    }

    /**
     * 添加字母到当前猜测
     */
    private void addCharToCurrentGuess(String key) {

    }

    private void createUIComponents() {
        settingsButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        keyboardPane = new KeyboardPanel();
        guessPane = new JPanel();
        guessPane.setBackground(Color.WHITE);
        guessPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 设置按钮
        letter4RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter5RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter6RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter7RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter8RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter9RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter10RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter11RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
    }
}
