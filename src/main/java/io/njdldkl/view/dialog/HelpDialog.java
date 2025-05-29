package io.njdldkl.view.dialog;

import io.njdldkl.view.component.RoundedLetterPanel;
import io.njdldkl.constant.ColorConstant;
import io.njdldkl.constant.DimensionConstant;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.util.ComponentUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Wordle游戏帮助界面的Java Swing实现
 * 此类创建一个模态对话框，显示Wordle游戏的玩法说明
 */
public class HelpDialog extends JDialog {

    // 定义字体常量
    private static final String ARIAL = "Arial";
    private static final String MICROSOFT_YAHEI = "Microsoft YaHei";
    private static final String SANS_SERIF = "SansSerif";
    private static final String SEGOE_UI_EMOJI = "Segoe UI Emoji";

    // 定义字体
    private static final Font TITLE_FONT = new Font(MICROSOFT_YAHEI, Font.BOLD, 24);
    private static final Font CONTENT_FONT = new Font(MICROSOFT_YAHEI, Font.PLAIN, 16);
    private static final Font LETTER_FONT = new Font(ARIAL, Font.BOLD, 24);
    private static final Font EMOJI_FONT = new Font(SEGOE_UI_EMOJI, Font.PLAIN, 24);

    /**
     * 构造函数
     *
     * @param parent 父窗口
     */
    public HelpDialog(JFrame parent) {
        super(parent, "帮助", true);
        initializeUI();
    }

    /**
     * 无参构造函数，用于独立运行
     */
    public HelpDialog() {
        super((JFrame) null, "帮助", true);
        initializeUI();
    }

    /**
     * 初始化用户界面
     */
    private void initializeUI() {
        // 设置窗口属性
        setMaximumSize(DimensionConstant.DIALOG_MAXIMUM);
        setMaximumSize(DimensionConstant.DIALOG_MINIMUM);
        setPreferredSize(DimensionConstant.DIALOG_PREFERRED);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 创建主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ColorConstant.BLANK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 添加标题和关闭按钮
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 添加游戏规则说明
        JLabel rulesLabel = createCenteredLabel("你需要在6次尝试中猜出隐藏的单词，字母的颜色会变化以显示你猜测的接近程度。");
        mainPanel.add(rulesLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 添加开始游戏的说明
        JLabel startLabel = createCenteredLabel("要开始游戏，只需输入任意单词，例如：");
        mainPanel.add(startLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 添加示例单词 "TABLE"
        JPanel tablePanel = createWordPanel(new String[]{"T", "A", "B", "L", "E"},
                new Color[]{ColorConstant.GRAY, ColorConstant.YELLOW, ColorConstant.GRAY, ColorConstant.YELLOW, ColorConstant.GREEN});
        mainPanel.add(tablePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 添加解释面板
        JPanel explanationPanel = createExplanationPanel();
        mainPanel.add(explanationPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // 添加下一步提示
        JLabel nextTryLabel = createCenteredLabel("再尝试一次以找出匹配的字母。");
        mainPanel.add(nextTryLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 添加示例单词"FLASH"
        JPanel flashPanel = createWordPanel(new String[]{"F", "L", "A", "S", "H"},
                new Color[]{ColorConstant.GREEN, ColorConstant.GREEN, ColorConstant.GREEN, ColorConstant.GRAY, ColorConstant.GRAY});
        mainPanel.add(flashPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 添加提示文本
        JLabel closeLabel = createCenteredLabel("很接近了！");
        mainPanel.add(closeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 添加示例单词"FLAME"
        JPanel flamePanel = createWordPanel(new String[]{"F", "L", "A", "M", "E"},
                new Color[]{ColorConstant.GREEN, ColorConstant.GREEN, ColorConstant.GREEN, ColorConstant.GREEN, ColorConstant.GREEN});
        mainPanel.add(flamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 添加成功提示
        JPanel successPanel = getSuccessPanel();
        mainPanel.add(successPanel);

        // 添加滚动面板以支持较小的屏幕
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // 添加到窗口
        add(scrollPane);

        pack();

        // 设置窗口居中显示（窗口中心点与屏幕中心点重合）
        ComponentUtils.setCenterWindowOnScreen(this);
    }

    /**
     * 创建标题面板
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorConstant.BLANK);

        JLabel titleLabel = new JLabel("如何玩");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    /**
     * 创建居中对齐的标签
     */
    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(CONTENT_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * 创建单词面板，显示一个单词的字母和颜色
     */
    private JPanel createWordPanel(String[] letters, Color[] colors) {
        JPanel panel = new JPanel();
        panel.setBackground(ColorConstant.BLANK);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (int i = 0; i < letters.length; i++) {
            panel.add(createLetterBox(letters[i], colors[i]));
        }

        return panel;
    }

    /**
     * 创建字母框
     */
    private JPanel createLetterBox(String letter, Color bgColor) {
        // 使用RoundedLetterPanel
        return new RoundedLetterPanel(DimensionConstant.LETTER_PANEL, letter,
                LETTER_FONT, bgColor, IntegerConstant.SHARP_RADIUS);
    }

    /**
     * 创建解释面板
     */
    private JPanel createExplanationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 灰色字母解释
        JPanel greyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        greyPanel.setBackground(panel.getBackground());
        greyPanel.add(createLetterBox("T", ColorConstant.GRAY));
        greyPanel.add(createLetterBox("B", ColorConstant.GRAY));
        JLabel jLabel1 = new JLabel(" 不在目标单词中。");
        jLabel1.setFont(CONTENT_FONT);
        greyPanel.add(jLabel1);
        panel.add(greyPanel);

        // 黄色字母解释
        JPanel yellowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yellowPanel.setBackground(panel.getBackground());
        yellowPanel.add(createLetterBox("A", ColorConstant.YELLOW));
        yellowPanel.add(createLetterBox("L", ColorConstant.YELLOW));
        JLabel jLabel2 = new JLabel(" 在单词中但位置错误。");
        jLabel2.setFont(CONTENT_FONT);
        yellowPanel.add(jLabel2);
        panel.add(yellowPanel);

        // 绿色字母解释
        JPanel greenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        greenPanel.setBackground(panel.getBackground());
        greenPanel.add(createLetterBox("E", ColorConstant.GREEN));
        JLabel jLabel3 = new JLabel(" 在单词中且位置正确。");
        jLabel3.setFont(CONTENT_FONT);
        greenPanel.add(jLabel3);
        panel.add(greenPanel);

        return panel;
    }

    /**
     * 创建成功面板
     */
    private static JPanel getSuccessPanel() {
        JPanel successPanel = new JPanel();
        successPanel.setBackground(ColorConstant.BLANK);
        successPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        successPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));

        JLabel successTextLabel = new JLabel("成功了！");
        successTextLabel.setFont(CONTENT_FONT);
        successTextLabel.setForeground(Color.BLACK);
        JLabel trophyLabel = new JLabel(new ImageIcon(HelpDialog.class.getResource("/icon/trophy.png")));

        successPanel.add(successTextLabel);
        successPanel.add(trophyLabel);
        return successPanel;
    }
}
