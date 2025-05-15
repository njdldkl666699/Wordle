package io.njdldkl.component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 圆角字母面板
 */
public class RoundedLetterPanel extends JPanel {

    private JLabel letterLabel;
    private int cornerRadius;
    private Color borderColor;
    private int borderWidth;
    private boolean hasBorder = false;

    /**
     * 构造函数
     *
     * @param letter       字母
     * @param dimension    面板大小
     * @param bgColor      背景颜色
     * @param cornerRadius 圆角半径
     * @param font         字体
     */
    public RoundedLetterPanel(char letter, Dimension dimension, Color bgColor, int cornerRadius, Font font) {
        setPreferredSize(dimension);
        setBackground(bgColor);
        setLayout(new GridBagLayout());
        this.cornerRadius = cornerRadius;
        setOpaque(false); // 设置为透明，以便绘制自定义形状

        letterLabel = new JLabel(String.valueOf(letter));
        letterLabel.setFont(font);
        letterLabel.setForeground(Color.WHITE);
        add(letterLabel);
    }

    /**
     * 构造函数，用于创建空白字母面板
     *
     * @param dimension    面板大小
     * @param bgColor      背景颜色
     * @param cornerRadius 圆角半径
     * @param borderColor  边框颜色
     * @param borderWidth  边框宽度
     */
    public RoundedLetterPanel(Dimension dimension, Color bgColor, int cornerRadius, Color borderColor, int borderWidth) {
        setPreferredSize(dimension);
        setBackground(bgColor);
        setLayout(new GridBagLayout());
        this.cornerRadius = cornerRadius;
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.hasBorder = true;
        setOpaque(false); // 设置为透明，以便绘制自定义形状
    }

    /**
     * 重写绘制组件方法实现圆角矩形
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 使用面板背景色填充圆角矩形
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2d.dispose();
    }

    /**
     * 重写绘制边框方法实现圆角边框
     */
    @Override
    protected void paintBorder(Graphics g) {
        if (hasBorder) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 设置边框颜色和宽度
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderWidth));

            // 绘制圆角矩形边框
            int offset = borderWidth / 2; // 边框居中绘制
            int width = getWidth() - borderWidth;
            int height = getHeight() - borderWidth;
            g2d.draw(new RoundRectangle2D.Float(offset, offset, width, height, cornerRadius, cornerRadius));

            g2d.dispose();
        }
    }
}
