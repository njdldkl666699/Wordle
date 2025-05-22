package io.njdldkl.view.frame;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.component.RoundedRadioButton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class WaitingRoomFrame extends BaseFrame {

    private JPanel contentPane;

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

    private JPanel roomIdPane;
    private JLabel roomIdLabel;
    private JTextField roomIdTextPane;
    private JPanel usersPane;
    private RoundedButton startButton;
    private RoundedButton leaveRoomButton;

    private MultiPlayService playService;

    // 当前用户
    private User currentUser;
    // 房间ID
    private int roomId;
    // 字母数量，只有房主的设置有效
    private int letterCount;

    public WaitingRoomFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        setupLetterButtonGroupListener();
        setupPlayService();
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));

        // TODO
        startButton.addActionListener(e -> {
        });
    }

    public void updateUI(User user, boolean host, String roomId) {
        this.currentUser = user;
        playService.registerUser(user, host, roomId);

        if (!host) {
            // 如果不是房主，则隐藏设置按钮和开始按钮
            settingsButton.setVisible(false);
            settingsPane.setVisible(false);
            startButton.setVisible(false);
        }

        letterCount = 5;
        letterButtonGroup.setSelected(letter5RadioButton.getModel(), true);

        // 清空用户列表面板
        usersPane.removeAll();
        usersPane.revalidate();
        usersPane.repaint();

        roomIdTextPane.setText(roomId);
    }

    /**
     * 设置多人游戏服务
     */
    private void setupPlayService() {
        // 如果之前有服务，先移除回调
        if (playService != null) {
            playService.removeUserListUpdateCallback(this::updateUsersList);
        }

        playService = new MultiPlayService();
        // 注册用户列表更新的回调
        playService.addUserListUpdateCallback(this::updateUsersList);
    }

    /**
     * 更新用户列表UI
     */
    private void updateUsersList(List<User> users) {
        usersPane.removeAll();
        users.forEach(this::addUserToRoom);

        usersPane.revalidate();
        usersPane.repaint();
    }

    private void addUserToRoom(User user) {
        JLabel userLabel = new JLabel(user.getName());
        // 如果是当前用户，可以添加特殊标记
        if (currentUser != null && user.getId().equals(currentUser.getId())) {
            userLabel.setText(user.getName() + " (你)");
        }
        // 如果是房主，添加标记
        if (playService != null && playService.isHost(user)) {
            userLabel.setText(userLabel.getText() + " [房主]");
        }
        usersPane.add(userLabel);
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

                    letterCount = newLetterCount;
                    settingsPane.setVisible(false);
                }
            });
        }
    }

    private void createUIComponents() {
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

        startButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
    }

}
