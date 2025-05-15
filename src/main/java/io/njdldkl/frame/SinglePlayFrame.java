package io.njdldkl.frame;

import io.njdldkl.WindowManager;
import io.njdldkl.component.RoundedButton;
import io.njdldkl.component.RoundedRadioButton;
import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;

import javax.swing.*;
import java.awt.*;

public class SinglePlayFrame extends BaseFrame {

    private JPanel contentPane;
    private RoundedButton settingsButton;
    private RoundedButton homeButton;
    private JPanel keyboardPane;
    private JScrollPane guessPane;

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

    public SinglePlayFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        // 返回主菜单按钮
        homeButton.addActionListener(e -> WindowManager.getInstance().showMenuFrame());

        // 设置按钮
        // TODO 折起时读取选择的数字
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));
    }

    private void createUIComponents() {
        settingsButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);

        // 设置按钮
        letter4RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter5RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter6RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter7RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter8RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter9RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR, ColorConstant.GREEN_COLOR);
        letter10RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR,ColorConstant.GREEN_COLOR);
        letter11RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL_COLOR,ColorConstant.GREEN_COLOR);
    }
}
