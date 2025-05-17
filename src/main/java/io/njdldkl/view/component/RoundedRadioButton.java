package io.njdldkl.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 自定义圆角单选按钮
 */
public class RoundedRadioButton extends JRadioButton {

    private int cornerRadius; // 圆角半径
    private Color selectedColor; // 选中状态颜色
    private Color normalColor; // 未选中状态颜色

    public RoundedRadioButton(int radius, Color normalColor, Color selectedColor) {
        this.cornerRadius = radius;
        this.selectedColor = selectedColor;
        this.normalColor = normalColor;

        setContentAreaFilled(false); // 取消默认填充
        setFocusPainted(false);      // 取消焦点边框
        setBorderPainted(false);     // 取消默认边框
        setOpaque(false);            // 设置为透明
        setForeground(Color.BLACK);  // 文本颜色
        setBackground(normalColor);  // 默认背景色
        setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 根据选中状态决定背景色
        if (isSelected()) {
            g2.setColor(selectedColor);
        } else if (getModel().isRollover()) {
            g2.setColor(selectedColor.brighter()); // 悬停时颜色变亮
        } else {
            g2.setColor(normalColor);
        }

        // 绘制圆角矩形
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // 这里不调用super.paintComponent是因为我们想完全自定义绘制，包括文本

        // 手动绘制文本
        FontMetrics fm = g2.getFontMetrics();
        Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
        String text = getText();

        // 计算文本位置使其居中
        int x = (textRect.width - fm.stringWidth(text)) / 2;
        int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();

        // 根据选中状态决定文字颜色
        if (isSelected()) {
            g2.setColor(Color.WHITE); // 选中时文字为白色
        } else {
            g2.setColor(getForeground()); // 未选中时使用默认前景色
        }

        g2.drawString(text, x, y);
        g2.dispose();
    }
}
