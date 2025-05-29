package io.njdldkl.view.dialog;

import io.njdldkl.Application;
import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.frame.BaseFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Random;

@Slf4j
public class EasterEggDialog extends JFrame {

    // 窗口刷新时间，单位毫秒
    private static final int REFRESH_DELAY = 16;

    // 关闭按钮图标
    private static final ImageIcon CLOSE_ICON =
            new ImageIcon(EasterEggDialog.class.getResource("/icon/close_48x.png"));
    private static final ImageIcon CLOSE_HOVERED_ICON =
            new ImageIcon(EasterEggDialog.class.getResource("/icon/close_hovered_48x.png"));

    private final JPanel contentPane;

    private final Random random = new Random();

    private final Runnable[] easterEggFunctions;

    private final Timer moveRightTimer;

    private final Timer ranbowBackgroundTimer;
    private float hue;

    private final Timer opacityTimer;

    public EasterEggDialog() {
        setTitle("彩蛋");
        setIconImage(Toolkit.getDefaultToolkit().getImage(BaseFrame.class.getResource("/icon/wordle.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setLayout(new BorderLayout());

        // 创建一个容器面板，设置为绿色背景
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(ColorConstant.GREEN);

        // Banner标签
        JLabel bannerLabel = new JLabel();
        bannerLabel.setText("<html><pre>" + Application.BANNER + "</pre></html>");
        bannerLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        bannerLabel.setForeground(Color.WHITE);
        bannerLabel.setOpaque(false);
        bannerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(bannerLabel, BorderLayout.CENTER);

        // 创建一个包含关闭按钮的面板
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        // 创建关闭按钮
        RoundedButton closeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        closeButton.setBackground(ColorConstant.LIGHT_GRAY);
        closeButton.setIcon(CLOSE_ICON);
        closeButton.setPressedIcon(CLOSE_HOVERED_ICON);
        closeButton.setRolloverIcon(CLOSE_HOVERED_ICON);
        closeButton.setSelectedIcon(CLOSE_HOVERED_ICON);
        closeButton.setRolloverSelectedIcon(CLOSE_HOVERED_ICON);
        closeButton.setPreferredSize(new Dimension(48, 48));

        closeButton.addActionListener(e -> {
            stopAllTimers();
            setVisible(false);
            dispose();
        });
        closePanel.add(closeButton);
        contentPane.add(closePanel, BorderLayout.NORTH);

        setContentPane(contentPane);

        moveRightTimer = new Timer(REFRESH_DELAY, e -> {
            Point location = getLocation();
            setLocation(location.x + 5, location.y);
            if (location.x > Toolkit.getDefaultToolkit().getScreenSize().width) {
                setLocation(-this.getSize().width, location.y);
            }
        });
        moveRightTimer.setCoalesce(false); // 不合并事件

        ranbowBackgroundTimer = new Timer(REFRESH_DELAY, e -> {
            // 递增色相值
            hue += 0.005f;
            if (hue > 1) {
                hue = 0;
            }

            // 使用HSB模型创建彩虹颜色
            Color rainbowColor = Color.getHSBColor(hue, 1f, 0.8f);
            contentPane.setBackground(rainbowColor);
        });

        opacityTimer = new Timer(REFRESH_DELAY, e -> {
            // 获取当前时间并计算透明度
            double time = System.currentTimeMillis() / 2000.0; // 2秒一个完整周期

            // 使用余弦函数来实现0.5-1.0的线性变化
            // (cos(x) + 1) / 4 + 0.5 会在0.5和1.0之间变化
            float opacity = (float) ((Math.cos(Math.PI * time) + 1) / 4 + 0.5);

            // 设置窗口透明度
            setOpacity(opacity);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopAllTimers();
            }
        });

        easterEggFunctions = new Runnable[]{
                this::moveRight,
                this::neverGonnaGiveYouUp,
                this::rainbowBackground,
                this::opacityWindow
        };
    }

    public void resetDialog() {
        setSize(533, 300);
        contentPane.setBackground(ColorConstant.GREEN);
        setOpacity(1.0f);
        ComponentUtils.setCenterWindowOnScreen(this);
        // 随机选择一个彩蛋函数
        int index = random.nextInt(easterEggFunctions.length);
        easterEggFunctions[index].run();
    }

    public void resetDialog(int functionIndex) {
        setSize(533, 300);
        ComponentUtils.setCenterWindowOnScreen(this);
        if (functionIndex < 0 || functionIndex >= easterEggFunctions.length) {
            throw new IllegalArgumentException("无效的函数索引: " + functionIndex);
        }
        easterEggFunctions[functionIndex].run();
    }

    /**
     * 停止所有计时器
     */
    private void stopAllTimers() {
        if (moveRightTimer.isRunning()) {
            moveRightTimer.stop();
        }
        if (ranbowBackgroundTimer.isRunning()) {
            ranbowBackgroundTimer.stop();
        }
        if (opacityTimer.isRunning()) {
            opacityTimer.stop();
        }
    }

    /**
     * 向右移动窗口
     */
    private void moveRight() {
        moveRightTimer.start();
    }

    /**
     * 打开链接到B站视频
     */
    private void neverGonnaGiveYouUp() {
        // 打开浏览器
        try {
            Desktop.getDesktop().browse(new URI("https://www.bilibili.com/video/BV1GJ411x7h7/"));
        } catch (Exception e) {
            log.error("打开链接失败", e);
        }
    }

    /**
     * 彩虹背景渐变效果
     */
    private void rainbowBackground() {
        // 重置色相值
        hue = 0;
        ranbowBackgroundTimer.start();
    }

    /**
     * 透明度渐变效果
     */
    private void opacityWindow() {
        opacityTimer.start();
    }

}
