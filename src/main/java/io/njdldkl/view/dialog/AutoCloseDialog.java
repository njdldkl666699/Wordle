package io.njdldkl.view.dialog;

import io.njdldkl.constant.DimensionConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>自动关闭对话框</p>
 * 失去焦点后自动关闭，超过一定时间自动关闭<br>
 * 带有圆角和窗口外阴影效果
 */
public class AutoCloseDialog extends RoundedShadowDialog {

    private final JLabel textLabel;

    private final Timer autoCloseTimer;

    public AutoCloseDialog(JFrame parent) {
        super(parent);

        // 考虑阴影大小，调整对话框尺寸
        Dimension originalSize = DimensionConstant.AUTO_CLOSE_DIALOG;
        setSize(new Dimension(
                originalSize.width + IntegerConstant.SHADOW_SIZE * 2,
                originalSize.height + IntegerConstant.SHADOW_SIZE * 2));

        ComponentUtils.setCenterWindowOnScreen(this);
        setResizable(false);

        // 设置文字内容
        textLabel = new JLabel();
        textLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 32));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setForeground(Color.BLACK);
        contentPane.add(textLabel, BorderLayout.CENTER);

        // 设置失去焦点后自动关闭
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });

        // 设置一定时间后自动关闭
        autoCloseTimer = new Timer(IntegerConstant.AUTO_CLOSE_TIME_MS, e -> dispose());
        autoCloseTimer.setRepeats(false);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            autoCloseTimer.start();
        }
        super.setVisible(b);
    }

    /**
     * 设置对话框的文本内容
     */
    public void setText(String text) {
        textLabel.setText(text);
        textLabel.revalidate();
        textLabel.repaint();
    }
}
