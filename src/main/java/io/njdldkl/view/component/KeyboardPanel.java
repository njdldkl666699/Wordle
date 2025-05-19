package io.njdldkl.view.component;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.enumerable.WordStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class KeyboardPanel extends JPanel {

    // 键盘布局（QWERTY布局）
    private static final String[] ROW1 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
    private static final String[] ROW2 = {"A", "S", "D", "F", "G", "H", "J", "K", "L"};
    private static final String[] ROW3 = {"DEL", "Z", "X", "C", "V", "B", "N", "M", "ENTER"};

    // 按键映射
    private final Map<String, RoundedButton> keyButtons = new HashMap<>();
    // 按键状态映射
    private final Map<String, WordStatus> keyStatusMap = new HashMap<>();

    // 按键样式常量
    private static final int BUTTON_HEIGHT = 50;
    private static final int SPECIAL_BUTTON_WIDTH = 108;
    private static final int BUTTON_GAP = 6;

    public KeyboardPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        initKeyboard();
    }

    private void initKeyboard() {
        // 创建三行面板
        JPanel row1Panel = createRowPanel(ROW1, 70);
        JPanel row2Panel = createRowPanel(ROW2, 78);
        JPanel row3Panel = createRowPanel(ROW3, 70);

        // 添加行间距
        add(row1Panel);
        add(Box.createVerticalStrut(BUTTON_GAP));
        add(row2Panel);
        add(Box.createVerticalStrut(BUTTON_GAP));
        add(row3Panel);
    }

    private JPanel createRowPanel(String[] keys, int buttonWidth) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, BUTTON_GAP, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 添加按钮到行
        for (String key : keys) {
            // 创建按钮
            RoundedButton button = new RoundedButton(IntegerConstant.SHARP_RADIUS);

            // 设置按钮大小
            Dimension buttonSize;
            if (key.equals("ENTER") || key.equals("DEL")) {
                buttonSize = new Dimension(SPECIAL_BUTTON_WIDTH, BUTTON_HEIGHT);
            } else {
                buttonSize = new Dimension(buttonWidth, BUTTON_HEIGHT);
            }
            button.setPreferredSize(buttonSize);

            // 设置按钮显示文本
            if (key.equals("ENTER")) {
                button.setText("Enter");
                button.setName("ENTER");
            } else if (key.equals("DEL")) {
                button.setIcon(new ImageIcon(getClass().getResource("/icon/backspace_48x.png")));
                button.setName("DEL");
            } else {
                button.setText(key);
                button.setName(key);
            }

            // 设置字体和颜色
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.setBackground(ColorConstant.LIGHT_GRAY);
            button.setForeground(Color.BLACK);

            // 保存按钮引用
            keyButtons.put(key, button);

            // 添加到行面板
            rowPanel.add(button);
        }

        return rowPanel;
    }

    /**
     * 设置按键监听器（按钮）
     */
    public void setKeyListener(ActionListener listener) {
        // 为所有已创建的按钮添加监听器
        for (RoundedButton button : keyButtons.values()) {
            button.addActionListener(listener);
        }
    }

    /**
     * 更新按键状态 - 正确位置
     */
    public void setCorrect(String key) {
        RoundedButton button = keyButtons.get(key);
        if (button != null) {
            button.setBackground(ColorConstant.GREEN);
            button.setForeground(Color.WHITE);
        }

        // 更新按键状态
        keyStatusMap.put(key, WordStatus.CORRECT);
    }

    /**
     * 更新按键状态 - 存在但位置错误
     */
    public void setWrongPosition(String key) {
        RoundedButton button = keyButtons.get(key);
        if (button != null) {
            button.setBackground(ColorConstant.YELLOW);
            button.setForeground(Color.WHITE);
        }

        // 更新按键状态
        WordStatus wordStatus = keyStatusMap.get(key);
        if (wordStatus != WordStatus.CORRECT) {
            keyStatusMap.put(key, WordStatus.WRONG_POSITION);
        }
    }

    /**
     * 更新按键状态 - 不存在
     */
    public void setAbsent(String key) {
        RoundedButton button = keyButtons.get(key);
        if (button != null) {
            button.setBackground(ColorConstant.GRAY);
            button.setForeground(Color.WHITE);
        }

        // 更新按键状态
        WordStatus wordStatus = keyStatusMap.get(key);
        if (wordStatus != WordStatus.CORRECT && wordStatus != WordStatus.WRONG_POSITION) {
            keyStatusMap.put(key, WordStatus.ABSENT);
        }
    }

    /**
     * 重置所有按键状态
     */
    public void resetKeyboard() {
        for (RoundedButton button : keyButtons.values()) {
            button.setBackground(ColorConstant.LIGHT_GRAY);
            button.setForeground(Color.BLACK);
        }
        keyStatusMap.clear();
    }
}
