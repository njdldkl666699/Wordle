package io.njdldkl.view.dialog;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BackHomeDialog extends RoundedShadowDialog {

    private final RoundedButton confirmButton;

    public BackHomeDialog(JFrame parent) {
        super(parent);

        setSize(320 + IntegerConstant.SHADOW_SIZE * 2, 180 + IntegerConstant.SHADOW_SIZE * 2);
        ComponentUtils.setCenterWindowOnScreen(this);

        // 使用继承的contentPane，不需要重新获取
        contentPane.setLayout(new BorderLayout(0, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 创建消息面板
        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BorderLayout());
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>确定要离开游戏并返回主菜单吗？<br>如果是房主，离开将会关闭房间。</div></html>");
        messageLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        contentPane.add(messagePanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // 创建确认按钮
        confirmButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        confirmButton.setText("确认");
        confirmButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        confirmButton.setPreferredSize(new Dimension(100, 36));
        confirmButton.setBackground(ColorConstant.GREEN);
        confirmButton.setForeground(Color.WHITE);

        // 创建取消按钮
        RoundedButton cancelButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        cancelButton.setText("取消");
        cancelButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        cancelButton.setPreferredSize(new Dimension(100, 36));
        cancelButton.setBackground(ColorConstant.GRAY);
        cancelButton.setForeground(Color.BLACK);

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // 添加按钮事件监听器
        cancelButton.addActionListener(e -> setVisible(false));
    }

    public void addConfirmButtonListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }

}
