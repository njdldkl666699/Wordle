package io.njdldkl.view.frame;

import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.RoundedButton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

@Slf4j
public class MenuFrame extends BaseFrame {

    private JPanel contentPane;

    private RoundedButton avatarButton;
    private JPanel usernamePanel;
    private JTextField usernameField;
    private RoundedButton helpButton;

    private JPanel titlePanel;
    private RoundedButton title1Button;
    private RoundedButton title2Button;
    private RoundedButton title3Button;
    private RoundedButton title4Button;
    private RoundedButton title5Button;
    private RoundedButton title6Button;

    private RoundedButton singlePlayButton;
    private RoundedButton multiPlayButton;

    private JPanel multiPlayPane;
    private JLabel createRoomLabel;
    private RoundedButton createRoomButton;
    private JLabel joinRoomLabel;
    private JTextField joinRoomTextField;
    private RoundedButton joinRoomButton;

    private final boolean[] titleButtonsPressed = new boolean[6];

    public MenuFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        // 头像按钮
        avatarButton.addActionListener(e -> handleAvatarButtonPress());

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
        multiPlayButton.addActionListener(e -> multiPlayPane.setVisible(!multiPlayPane.isVisible()));
    }

    public User getUser() {
        String username = usernameField.getText();
        ImageIcon avatar = (ImageIcon) avatarButton.getIcon();
        return new User(username, avatar);
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

    private void handleAvatarButtonPress() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择头像");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        // 设置文件过滤器，只显示PNG文件
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "图片", "png", "jpg", "jpeg");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false); // 禁用"所有文件"选项

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // 缩放头像并设置
            ImageIcon avatar = new ImageIcon(filePath);
            Image scaledInstance = avatar.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledInstance);
            avatarButton.setIcon(imageIcon);
        }
    }

    private void createUIComponents() {
        avatarButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        helpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);

        title1Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title2Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title3Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title4Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title5Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        title6Button = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);

        singlePlayButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        multiPlayButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);

        createRoomButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        joinRoomButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);

        multiPlayPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // 抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 使用当前面板的背景色填充圆角矩形
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(),
                        IntegerConstant.SMOOTH_RADIUS, IntegerConstant.SMOOTH_RADIUS);

                g2d.dispose();
            }
        };
    }
}
