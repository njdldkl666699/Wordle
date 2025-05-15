package io.njdldkl.frame;

import io.njdldkl.WindowManager;
import io.njdldkl.component.RoundedButton;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Arrays;

@Slf4j
public class MenuFrame extends BaseFrame {

    private JPanel contentPane;
    private JTextField usernameField;
    private RoundedButton helpButton;
    private RoundedButton title1Button;
    private RoundedButton title2Button;
    private RoundedButton title3Button;
    private RoundedButton title4Button;
    private RoundedButton title5Button;
    private RoundedButton title6Button;
    private RoundedButton singlePlayButton;
    private RoundedButton multiPlayButton;

    private final boolean[] titleButtonsPressed = new boolean[6];

    public MenuFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        // 帮助按钮
        helpButton.addActionListener(e -> WindowManager.getInstance().showHelpDialog());

        // 标题按钮
        title1Button.addActionListener(e -> handleTitleButtonPress(0));
        title2Button.addActionListener(e -> handleTitleButtonPress(1));
        title3Button.addActionListener(e -> handleTitleButtonPress(2));
        title4Button.addActionListener(e -> handleTitleButtonPress(3));
        title5Button.addActionListener(e -> handleTitleButtonPress(4));
        title6Button.addActionListener(e -> handleTitleButtonPress(5));

        // 单人游戏按钮
        singlePlayButton.addActionListener(e -> WindowManager.getInstance().showSinglePlayFrame());

        // 多人对战按钮
        multiPlayButton.addActionListener(e -> {
            // TODO
        });
    }

    /**
     * 顺序按下title1, title2, title3, title4, title5, title6时触发彩蛋
     */
    private void handleTitleButtonPress(int i) {
        // 检查是否按顺序
        for (int j = 0; j < i; j++) {
            if (!titleButtonsPressed[j]) {
                // 按错顺序，全部重置
                Arrays.fill(titleButtonsPressed, false);
                return;
            }
        }
        // 当前按钮未按下且前面都已按下
        if (!titleButtonsPressed[i]) {
            titleButtonsPressed[i] = true;
        }

        // 如果全部按下，触发彩蛋并重置
        boolean allPressed = true;
        for (boolean pressed : titleButtonsPressed) {
            if (!pressed) {
                allPressed = false;
                break;
            }
        }
        if (allPressed) {
            log.debug("触发彩蛋");
            WindowManager.getInstance().showEasterEggDialog();
            Arrays.fill(titleButtonsPressed, false);
        }
    }

    private void createUIComponents() {
        helpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title1Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title2Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title3Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title4Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title5Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title6Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        singlePlayButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        multiPlayButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
    }
}
