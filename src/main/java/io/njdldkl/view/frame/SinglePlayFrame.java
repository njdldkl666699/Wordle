package io.njdldkl.view.frame;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.PlayService;
import io.njdldkl.service.impl.SinglePlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.component.RoundedRadioButton;
import io.njdldkl.view.dialog.GameOverDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

@Slf4j
public class SinglePlayFrame extends BaseFrame implements GameOverDialogHandler {

    private JPanel contentPane;
    private final PlayFrameHelper playFrameHelper;
    private PlayService singlePlayService;

    private RoundedButton homeButton;
    private RoundedButton giveUpButton;

    private JScrollPane guessScrollPane;
    private JPanel guessPane;
    private KeyboardPanel keyboardPane;

    private RoundedButton settingsButton;
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

    // 字母数量
    protected int letterCount;

    public SinglePlayFrame(User user) {
        setContentPane(contentPane);

        singlePlayService = new SinglePlayService(user);

        playFrameHelper = PlayFrameHelper.builder()
                .frame(this)
                .gameOverDialogHandler(this)
                .playService(singlePlayService)
                .guessScrollPane(guessScrollPane)
                .guessPane(guessPane)
                .keyboardPane(keyboardPane)
                .build();
        playFrameHelper.initUI();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        playFrameHelper.setupListeners();
        // 返回主菜单按钮
        homeButton.addActionListener(e -> WindowManager.getInstance().showMenuFrame());
        // 认输按钮
        giveUpButton.addActionListener(e -> handleGiveUpButtonPressed());
        // 设置面板
        setupLetterButtonGroupListener();
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));
    }

    /**
     * 开始游戏
     */
    public void startGame(User user) {
        letterCount = 5;
        letterButtonGroup.setSelected(letter5RadioButton.getModel(), true);
        playFrameHelper.updateGuessPane(letterCount);
        singlePlayService.registerUser(user, true, null);
        singlePlayService.requestStartGame(letterCount);
    }

    private void handleGiveUpButtonPressed(){
        // 创建游戏结束对话框
        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.setTitle("游戏失败！");
        gameOverDialog.setWord(singlePlayService.getAnswer());

        gameOverDialog.addBackHomeButtonListener(e -> {
            gameOverDialog.setVisible(false);
            setVisible(false);
            WindowManager.getInstance().showMenuFrame();
            gameOverDialog.dispose();
        });

        gameOverDialog.setVisible(true);
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

                    // 立即更新字母数量
                    letterCount = newLetterCount;
                    keyboardPane.resetKeyboard();
                    // 更新面板
                    playFrameHelper.updateGuessPane(newLetterCount);
                    // 更新游戏
                    singlePlayService.requestStartGame(letterCount);

                    // 关闭设置面板
                    settingsPane.setVisible(false);
                }
            });
        }
    }

    private void createUIComponents() {
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        giveUpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        keyboardPane = new KeyboardPanel();
        guessPane = new JPanel();
        guessPane.setBackground(Color.WHITE);
        guessPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 设置按钮
        settingsButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        letter4RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter5RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter6RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter7RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter8RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter9RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter10RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
        letter11RadioButton = new RoundedRadioButton(IntegerConstant.SHARP_RADIUS, ColorConstant.RADIO_NORMAL, ColorConstant.GREEN);
    }

    @Override
    public void showWinGameOverDialog(Word answer) {
        log.info("创建游戏结束胜利对话框");
        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.addBackHomeButtonListener(e -> {
            gameOverDialog.setVisible(false);
            setVisible(false);
            WindowManager.getInstance().showMenuFrame();
            gameOverDialog.dispose();
        });

        gameOverDialog.setTitle("游戏成功！");
        gameOverDialog.setWord(answer);
        gameOverDialog.setVisible(true);
    }

    @Override
    public void showLoseGameOverDialog(Word answer) {
        log.info("创建游戏结束失败对话框");
        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.addBackHomeButtonListener(e -> {
            gameOverDialog.setVisible(false);
            setVisible(false);
            WindowManager.getInstance().showMenuFrame();
            gameOverDialog.dispose();
        });

        gameOverDialog.setTitle("游戏失败！");
        gameOverDialog.setWord(answer);
        gameOverDialog.setVisible(true);
    }
}
