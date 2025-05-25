package io.njdldkl.view.frame;

import com.google.common.eventbus.Subscribe;
import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.event.GameStartedEvent;
import io.njdldkl.pojo.event.HostLeftEvent;
import io.njdldkl.pojo.event.UserListUpdatedEvent;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.component.RoundedRadioButton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
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
    private JLabel waitingLabel;

    @Getter
    private MultiPlayService multiPlayService;

    // 当前用户
    @Getter
    private User currentUser;
    // 字母数量，只有房主的设置有效
    @Getter
    private int letterCount;

    public WaitingRoomFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        multiPlayService = new MultiPlayService();
        // 注册为事件监听器
        multiPlayService.registerEventListener(this);

        setupLetterButtonGroupListener();
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));
        startButton.addActionListener(e -> multiPlayService.requestStartGame(letterCount));
        leaveRoomButton.addActionListener(e -> leaveRoom());
    }

    public void updateUI(User user, boolean host, String roomId) {
        this.currentUser = user;
        multiPlayService.registerUser(user, host, roomId);

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
     * 更新用户列表UI
     */
    @Subscribe
    public void onUserListUpdated(UserListUpdatedEvent event) {
        SwingUtilities.invokeLater(() -> {
            usersPane.removeAll();
            List<User> users = event.users();
            users.forEach(this::addUserToRoom);

            usersPane.revalidate();
            usersPane.repaint();
        });
    }

    private void addUserToRoom(User user) {
        // 创建用户面板
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.setPreferredSize(new Dimension(180, 90)); // 设置固定大小
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 添加用户头像
        JLabel avatarLabel = new JLabel(user.getAvatar());
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userPanel.add(avatarLabel, BorderLayout.CENTER);

        // 添加用户名称
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userPanel.add(nameLabel, BorderLayout.SOUTH);

        // 设置背景色
        if (multiPlayService != null && multiPlayService.isHost(user.getId())) {
            // 房主背景色为绿色
            userPanel.setBackground(ColorConstant.GREEN);
            nameLabel.setText(nameLabel.getText() + " [房主]");
        }

        if (currentUser != null && user.getId().equals(currentUser.getId())) {
            // 当前用户背景色为黄色，但如果是房主则保持绿色
            if (multiPlayService != null && !multiPlayService.isHost(user.getId())) {
                userPanel.setBackground(ColorConstant.YELLOW);
            }
            nameLabel.setText(nameLabel.getText() + " (你)");
        }

        // 为面板添加边框
        userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        usersPane.add(userPanel);
    }

    @Subscribe
    public void onHostLeft(HostLeftEvent event) {
        SwingUtilities.invokeLater(() -> WindowManager.getInstance().showMenuFrame());
    }

    private void leaveRoom() {
        if (multiPlayService != null) {
            multiPlayService.leaveRoom();
            WindowManager.getInstance().showMenuFrame();
        }
    }

    @Subscribe
    public void onGameStarted(GameStartedEvent event) {
        // 显示游戏界面
        SwingUtilities.invokeLater(() -> WindowManager.getInstance().showMultiPlayFrame());
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
        leaveRoomButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
    }

}
