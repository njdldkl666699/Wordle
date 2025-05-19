package io.njdldkl.view.dialog;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * 有阴影，无边框，圆角的对话框
 */
public class RoundedShadowDialog extends JDialog {

    private final JPanel windowPane;

    protected final JPanel contentPane;

    public RoundedShadowDialog(JFrame parent) {
        super(parent);
        setModal(true);
        setUndecorated(true);

        // 创建窗口面板
        windowPane = createWindowPane();
        windowPane.setBackground(Color.WHITE);
        windowPane.setLayout(new BorderLayout());
        // 设置东南西北的空内容
        windowPane.add(Box.createHorizontalStrut(IntegerConstant.SHADOW_SIZE), BorderLayout.WEST);
        windowPane.add(Box.createHorizontalStrut(IntegerConstant.SHADOW_SIZE), BorderLayout.EAST);
        windowPane.add(Box.createVerticalStrut(IntegerConstant.SHADOW_SIZE), BorderLayout.NORTH);
        windowPane.add(Box.createVerticalStrut(IntegerConstant.SHADOW_SIZE), BorderLayout.SOUTH);

        // 设置面板为透明，以便显示圆角
        setContentPane(windowPane);
        setBackground(new Color(0, 0, 0, 0));

        // 创建内容面板，用于放置实际内容
        contentPane = createContentPane();
        contentPane.setBackground(Color.WHITE);
        windowPane.add(contentPane, BorderLayout.CENTER);

        // 设置窗口形状为圆角矩形
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new RoundRectangle2D.Double(
                        0, 0, getWidth(), getHeight(),
                        IntegerConstant.SMOOTH_RADIUS * 2, IntegerConstant.SMOOTH_RADIUS * 2));
            }
        });
    }

    private JPanel createWindowPane() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制阴影
                g2d.setColor(ColorConstant.SHADOW);
                for (int i = 0; i < IntegerConstant.SHADOW_SIZE; i++) {
                    g2d.fill(new RoundRectangle2D.Float(
                            i, i,
                            getWidth() - i * 2,
                            getHeight() - i * 2,
                            IntegerConstant.SMOOTH_RADIUS + i, IntegerConstant.SMOOTH_RADIUS + i));
                }

                // 绘制内容区域背景
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(
                        IntegerConstant.SHADOW_SIZE, IntegerConstant.SHADOW_SIZE,
                        getWidth() - IntegerConstant.SHADOW_SIZE * 2,
                        getHeight() - IntegerConstant.SHADOW_SIZE * 2,
                        IntegerConstant.SMOOTH_RADIUS, IntegerConstant.SMOOTH_RADIUS));

                g2d.dispose();
            }
        };
    }

    private JPanel createContentPane() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制内容面板背景
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(
                        0, 0,
                        getWidth(),
                        getHeight(),
                        IntegerConstant.SMOOTH_RADIUS, IntegerConstant.SMOOTH_RADIUS));

                g2d.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false; // 允许透明，以便圆角可见
            }
        };
    }
}
