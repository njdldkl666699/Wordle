package io.njdldkl.view.frame;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.net.Client;
import io.njdldkl.net.Server;
import io.njdldkl.pojo.User;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.component.RoundedRadioButton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Enumeration;

@Slf4j
public class WaitingRoomFrame extends BaseFrame {

    private JPanel contentPane;
    private Client client;
    private Server server;

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

    // 房间ID
    private int roomId;
    // 字母数量，只有房主的设置有效
    private int letterCount;

    public WaitingRoomFrame() {
        setContentPane(contentPane);
        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        setupLetterButtonGroupListener();
        settingsButton.addActionListener(e -> settingsPane.setVisible(!settingsPane.isVisible()));

        // TODO
        startButton.addActionListener(e->{});
    }

    public void updateUI(User user) {
        if(!user.isHost()){
            // 如果不是房主，则隐藏设置按钮和开始按钮
            settingsButton.setVisible(false);
            settingsPane.setVisible(false);
            startButton.setVisible(false);
        }

        letterCount = 5;
        letterButtonGroup.setSelected(letter5RadioButton.getModel(), true);

        usersPane.removeAll();
        usersPane.revalidate();
        usersPane.repaint();

        // TODO 设置房间ID
        roomIdTextPane.setText("123456");
    }

    private void addUserToRoom(User user) {
        // TODO 添加用户到房间
        JLabel userLabel = new JLabel(user.getName());
        usersPane.add(userLabel);
        usersPane.revalidate();
        usersPane.repaint();
    }

    /**
     * 为所有字母数量按钮添加监听器
     */
    private void setupLetterButtonGroupListener(){
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

    private void createUIComponents(){
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
}
