package io.njdldkl.view.frame;

import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.dialog.BackHomeDialog;
import io.njdldkl.view.dialog.GameOverDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class MultiPlayFrame extends BaseFrame {

    private JPanel contentPane;
    private final PlayFrameHelper playFrameHelper;
    private MultiPlayService multiPlayService;

    private RoundedButton homeButton;
    private RoundedButton giveUpButton;

    private JScrollPane guessScrollPane;
    private JPanel guessPane;
    private KeyboardPanel keyboardPane;

    private BackHomeDialog backHomeDialog;

    private JPanel usersPane;

    private User currentUser;

    public MultiPlayFrame() {
        setContentPane(contentPane);

        playFrameHelper = PlayFrameHelper.builder()
                .frame(this)
                .guessScrollPane(guessScrollPane)
                .guessPane(guessPane)
                .keyboardPane(keyboardPane)
                .build();
        playFrameHelper.initUI();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        backHomeDialog = createBackHomeDialog();

        playFrameHelper.setupListeners();
        // 返回主菜单按钮
        homeButton.addActionListener(e -> backHomeDialog.setVisible(true));
        // 认输按钮
        giveUpButton.addActionListener(e -> handleGiveUpButtonPressed());
    }

    /**
     * <p>开启新的一局游戏</p>
     * 多人游戏下，从waitingRoomFrame传入的字母数量
     */
    public void startGame(MultiPlayService playService, User currentUser, int letterCount) {
        // 更新游戏
        multiPlayService = playService;
        this.currentUser = currentUser;
        // 更新面板
        playFrameHelper.updateGuessPane(letterCount);
    }

    private void handleGiveUpButtonPressed() {
        // 先获取答案
        Word answer = multiPlayService.getAnswer();

        // 创建游戏结束对话框
        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.setTitle("游戏失败！");
        gameOverDialog.setWord(answer);

        // 为返回主菜单按钮添加监听器
        gameOverDialog.addBackHomeButtonListener(event -> {
            // 处理网络断开
            multiPlayService.leaveRoom(currentUser);
            // 关闭对话框和游戏界面
            gameOverDialog.setVisible(false);
            setVisible(false);
            // 显示主菜单
            WindowManager.getInstance().showMenuFrame();
        });

        // 显示游戏结束对话框
        gameOverDialog.setVisible(true);
    }

    private BackHomeDialog createBackHomeDialog() {
        backHomeDialog = new BackHomeDialog(this);
        backHomeDialog.addConfirmButtonListener(e -> {
            // 处理网络断开
            if (multiPlayService != null) {
                // 通知服务离开房间
                multiPlayService.leaveRoom(currentUser);
                log.info("用户离开房间并返回主菜单");
            }
            backHomeDialog.setVisible(false);
            // 返回主菜单
            WindowManager.getInstance().showMenuFrame();
        });
        return backHomeDialog;
    }

    private void createUIComponents() {
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        giveUpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        keyboardPane = new KeyboardPanel();
        guessPane = new JPanel();
        guessPane.setBackground(Color.WHITE);
        guessPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
