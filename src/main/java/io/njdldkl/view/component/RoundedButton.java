package io.njdldkl.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {

    // 圆角半径
    private int cornerRadius;

    public RoundedButton(int radius) {
        super();
        this.cornerRadius = radius;
        setContentAreaFilled(false); // 关键：取消默认填充
        setFocusPainted(false);      // 取消焦点边框
        setBorderPainted(false);    // 取消默认边框
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆角背景
        if (getModel().isPressed()) {
            g2.setColor(getBackground().darker()); // 按下时颜色变深
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().darker()); // 悬停时颜色变亮
        } else {
            g2.setColor(getBackground());
        }

        // 绘制圆角矩形
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2.dispose();

        super.paintComponent(g); // 绘制文字和图标
    }

    @Override
    protected void paintBorder(Graphics g) {
        if(!isBorderPainted()){
            return;
        }
        // 可选：绘制自定义边框（如需要）
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
        g2.dispose();
    }
}
