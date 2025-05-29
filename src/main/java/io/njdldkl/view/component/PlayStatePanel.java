package io.njdldkl.view.component;

import io.njdldkl.constant.ColorConstant;

import javax.swing.*;
import java.awt.*;

public class PlayStatePanel extends JPanel {

    public PlayStatePanel(ImageIcon avatar, String name, int correctCount, int wrongPositionCount) {
        setLayout(new BorderLayout(5, 0));
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new Dimension(100, 50));
        setPreferredSize(new Dimension(100, 50));
        setBackground(Color.WHITE);

        // 左侧头像区域
        JLabel avatarLabel = new JLabel(avatar);
        avatarLabel.setPreferredSize(new Dimension(48, 48));
        add(avatarLabel, BorderLayout.WEST);

        // 右侧信息区域
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);

        // 右上名称区域
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 9));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(nameLabel);

        // 右下状态区域
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(1, 2, 0, 0));
        statusPanel.setBackground(Color.WHITE);

        // 左侧正确计数面板
        JPanel correctPanel = new JPanel();
        correctPanel.setBackground(ColorConstant.GREEN);
        JLabel correctLabel = new JLabel(String.valueOf(correctCount));
        correctLabel.setFont(new Font("Arial", Font.BOLD, 12));
        correctLabel.setForeground(Color.WHITE);
        correctLabel.setHorizontalAlignment(SwingConstants.CENTER);
        correctPanel.add(correctLabel);

        // 右侧位置错误计数面板
        JPanel wrongPosPanel = new JPanel();
        wrongPosPanel.setBackground(ColorConstant.YELLOW);
        JLabel wrongPosLabel = new JLabel(String.valueOf(wrongPositionCount));
        wrongPosLabel.setFont(new Font("Arial", Font.BOLD, 12));
        wrongPosLabel.setForeground(Color.WHITE);
        wrongPosLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wrongPosPanel.add(wrongPosLabel);

        // 添加两个状态面板到状态区域
        statusPanel.add(correctPanel);
        statusPanel.add(wrongPosPanel);

        // 添加状态区域到信息面板
        infoPanel.add(statusPanel);

        // 将信息面板添加到用户卡片
        add(infoPanel, BorderLayout.CENTER);
    }
}
