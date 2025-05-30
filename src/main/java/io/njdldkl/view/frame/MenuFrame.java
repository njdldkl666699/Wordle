package io.njdldkl.view.frame;

import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.User;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.util.IpRoomIdUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.dialog.AutoCloseDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

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

    private final User user;

    public MenuFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        user = new User(UUID.randomUUID(), "User", (ImageIcon) avatarButton.getIcon());

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

        // 创建房间按钮
        createRoomButton.addActionListener(e -> WindowManager.getInstance().showCreateRoomFrame());

        // 加入房间按钮
        joinRoomButton.addActionListener(e -> handleJoinRoomButtonPress());
    }

    public User getUser() {
        user.setName(usernameField.getText());
        user.setAvatar((ImageIcon) avatarButton.getIcon());
        return user;
    }

    public String getRoomId() {
        return joinRoomTextField.getText();
    }

    /**
     * 顺序按下title1, title2, title3, title4, title5, title6时触发彩蛋
     * 如果点击不按顺序或重复点击已按下的按钮，将重置进度
     */
    private void handleTitleButtonPress(int buttonIndex) {
        // 检查是否是下一个要按的按钮
        int nextButtonToPress = 0;
        for (int i = 0; i < titleButtonsPressed.length; i++) {
            if (!titleButtonsPressed[i]) {
                nextButtonToPress = i;
                break;
            }
        }

        // 如果点击的不是下一个要按的按钮，重置所有状态
        if (buttonIndex != nextButtonToPress) {
            log.debug("彩蛋进度重置：点击了错误的按钮");
            Arrays.fill(titleButtonsPressed, false);
            return;
        }

        // 标记当前按钮为已按下
        titleButtonsPressed[buttonIndex] = true;
        log.debug("彩蛋进度：按下按钮 {}", buttonIndex + 1);

        // 检查是否所有按钮都已按下
        boolean allPressed = true;
        for (boolean pressed : titleButtonsPressed) {
            if (!pressed) {
                allPressed = false;
                break;
            }
        }

        // 如果全部按下，触发彩蛋并重置
        if (allPressed) {
            log.info("触发彩蛋");
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

    /**
     * <p>点击加入房间按钮时的处理</p>
     * 进行连接测试<br>
     * 如果连接成功，跳转到等待房间界面<br>
     * 如果连接失败，弹出提示框<br>
     */
    private void handleJoinRoomButtonPress() {
        String roomId = joinRoomTextField.getText().trim();

        // 显示连接中提示
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        joinRoomButton.setEnabled(false);
        joinRoomButton.setText("连接中...");

        // 使用SwingWorker在后台线程执行连接测试
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return canConnectToRoom(roomId);
            }

            @Override
            protected void done() {
                try {
                    boolean canConnect = get();
                    if (canConnect) {
                        // 连接成功，跳转到等待房间
                        WindowManager.getInstance().showJoinRoomFrame();
                    } else {
                        // 连接失败
                        log.warn("连接失败");
                        connectFailedDialog.setVisible(true);
                    }
                } catch (Exception ex) {
                    log.error("连接验证异常", ex);
                    connectFailedDialog.setVisible(true);
                } finally {
                    // 恢复界面状态
                    setCursor(Cursor.getDefaultCursor());
                    joinRoomButton.setEnabled(true);
                    joinRoomButton.setText("加入房间");
                }
            }

            private final AutoCloseDialog connectFailedDialog;
            // 实例初始化块，会在创建对象时执行
            {
                connectFailedDialog = new AutoCloseDialog(MenuFrame.this);
                connectFailedDialog.setText("连接失败");
            }
        };

        worker.execute();
    }

    /**
     * 连接测试
     */
    private boolean canConnectToRoom(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            log.warn("房间ID不能为空");
            return false;
        }

        String host = IpRoomIdUtils.roomIdToIp(roomId);
        Socket socket = null;

        try {
            // 尝试连接，设置超时时间为2秒
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, IntegerConstant.PORT), 2000);
            return true;
        } catch (IOException e) {
            log.warn("连接房间失败: {}", e.getMessage());
            return false;
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("关闭测试连接失败", e);
                }
            }
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
