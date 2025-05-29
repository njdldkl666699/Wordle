package io.njdldkl.view.dialog;

import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.Word;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;

public class GameOverDialog extends RoundedShadowDialog {

    private final JPanel titlePanel;
    private final JLabel titleLabel;
    private RoundedButton backHomeButton;

    private final JPanel textPanel;

    public GameOverDialog(JFrame parent) {
        super(parent);
        Dimension originalSize = new Dimension(270, 480);
        setSize(originalSize.width + IntegerConstant.SHADOW_SIZE * 2,
                originalSize.height + IntegerConstant.SHADOW_SIZE * 2);
        ComponentUtils.setCenterWindowOnScreen(this);
        setResizable(false);

        // 设置内容面板
        contentPane.setLayout(new BorderLayout(0, 0));

        // 创建一个主内容面板，使用 BorderLayout 控制三个主要组件
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // 设置标题面板
        titlePanel = createTitlePanel();
        titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setPreferredSize(new Dimension(originalSize.width, 48));
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 设置文本面板
        textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(textPanel, BorderLayout.CENTER);

        // 创建底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 设置关闭按钮
        backHomeButton = new RoundedButton(IntegerConstant.SHARP_RADIUS);
        backHomeButton.setText("返回菜单");
        backHomeButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        backHomeButton.setBackground(ColorConstant.GREEN);
        backHomeButton.setForeground(Color.WHITE);
        backHomeButton.setPreferredSize(new Dimension(135, 36));
        buttonPanel.add(backHomeButton);

        backHomeButton.addActionListener(e -> dispose());
    }

    public void addBackHomeButtonListener(ActionListener listener) {
        backHomeButton.addActionListener(listener);
    }

    @Override
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    private Word currentWord;

    /**
     * 设置对话框的文本内容
     *
     * @param word 要显示的单词对象
     */
    public void setWord(Word word) {
        // 保存当前设置的单词
        currentWord = word;

        // 更新文本面板内容
        updateTextPanel();
    }

    private JPanel extraInfoPanel;

    /**
     * 添加额外信息面板到文本面板
     *
     * @param panel 要添加的面板
     */
    public void addExtraInfoPanel(JPanel panel) {
        // 保存额外信息面板的引用
        extraInfoPanel = panel;

        // 如果设置过单词，需要重新显示所有内容
        if (currentWord != null) {
            updateTextPanel();
        } else {
            // 仅显示额外信息面板
            textPanel.removeAll();
            textPanel.add(extraInfoPanel);
            textPanel.add(Box.createVerticalStrut(20)); // 添加间距
            textPanel.revalidate();
            textPanel.repaint();
        }
    }

    /**
     * 更新文本面板内容，显示额外信息面板和单词信息
     */
    private void updateTextPanel() {
        textPanel.removeAll();

        // 如果有额外信息面板，先添加它
        if (extraInfoPanel != null) {
            textPanel.add(extraInfoPanel);
            textPanel.add(Box.createVerticalStrut(20)); // 添加间距
        }

        // 添加一个垂直空白区域，让内容看起来更居中
        textPanel.add(Box.createVerticalGlue());

        // 使用自定义面板包装文本组件，以便控制对齐方式
        JPanel textContentPanel = new JPanel();
        textContentPanel.setLayout(new BoxLayout(textContentPanel, BoxLayout.Y_AXIS));
        textContentPanel.setOpaque(false);
        textContentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel wordTitleLabel = new JLabel("答案是");
        wordTitleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        wordTitleLabel.setForeground(Color.BLACK);
        wordTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textContentPanel.add(wordTitleLabel);
        textContentPanel.add(Box.createVerticalStrut(15));

        JLabel wordLabel = new JLabel(currentWord.getWord());
        wordLabel.setFont(new Font("Arial", Font.BOLD, 32));
        wordLabel.setForeground(Color.BLACK);
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textContentPanel.add(wordLabel);
        textContentPanel.add(Box.createVerticalStrut(20));

        // 使用文本区域来显示可能很长的释义
        JTextArea meaningArea = new JTextArea(currentWord.getMeaning());
        meaningArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        meaningArea.setForeground(Color.BLACK);
        meaningArea.setBackground(Color.WHITE);
        meaningArea.setLineWrap(true);
        meaningArea.setWrapStyleWord(true);
        meaningArea.setEditable(false);
        meaningArea.setBorder(null);

        // 创建滚动面板来容纳文本区域
        JScrollPane scrollPane = new JScrollPane(meaningArea);
        // 设置滚动面板的最大尺寸为很大
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 设置滚动面板的首选尺寸
        int textWidth = 230;
        int textHeight = Math.min(150, meaningArea.getPreferredSize().height);
        scrollPane.setPreferredSize(new Dimension(textWidth, textHeight));

        textContentPanel.add(scrollPane);
        textPanel.add(textContentPanel);
        textPanel.add(Box.createVerticalGlue());

        textPanel.revalidate();
        textPanel.repaint();
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制带有特定圆角的标题背景
                g2d.setColor(getBackground());

                // 创建自定义路径，只有顶部两个角是圆角
                int width = getWidth();
                int height = getHeight();
                int radius = IntegerConstant.SMOOTH_RADIUS / 2;

                // 创建路径，只在顶部设置圆角
                Path2D.Float path = new Path2D.Float();
                path.moveTo(0, radius);
                // 左上角圆角
                path.quadTo(0, 0, radius, 0);
                // 顶部直线
                path.lineTo(width - radius, 0);
                // 右上角圆角
                path.quadTo(width, 0, width, radius);
                // 右侧直线
                path.lineTo(width, height);
                // 底部直线
                path.lineTo(0, height);
                // 左侧直线
                path.closePath();

                g2d.fill(path);
                g2d.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false; // 设置为透明，以便圆角可见
            }
        };
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(ColorConstant.GRAY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return titlePanel;
    }
}
