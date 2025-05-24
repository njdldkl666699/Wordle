package io.njdldkl.view.frame;

import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.dialog.BackHomeDialog;
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
                .giveUpButton(giveUpButton)
                .guessScrollPane(guessScrollPane)
                .guessPane(guessPane)
                .keyboardPane(keyboardPane)
                .build();
        playFrameHelper.initUI();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        backHomeDialog = createBackHomeDialog();

        playFrameHelper.setupListeners();
        homeButton.addActionListener(e -> backHomeDialog.setVisible(true));
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
