import io.njdldkl.component.RoundedLetterPanel;

import javax.swing.*;
import java.awt.*;

public class LetterPanelTest {

    public static void main(String[] args) {
        // 创建一个 JFrame 用于测试
        JFrame frame = new JFrame("LetterPanel 测试");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        // 创建 LetterPanel 实例
        RoundedLetterPanel roundedLetterPanel = new RoundedLetterPanel('A',new Dimension(50,50),Color.GREEN,20, new Font("Arial", Font.BOLD, 20));
        RoundedLetterPanel blankRoundedLetterPanel = new RoundedLetterPanel(new Dimension(50,50),Color.GREEN,20,Color.RED,2);
        roundedLetterPanel.setBounds(50, 50, 300, 200); // 设置位置和大小
        blankRoundedLetterPanel.setBounds(400, 300, 300, 200); // 设置位置和大小

        // 将 LetterPanel 添加到 JFrame
        frame.add(roundedLetterPanel);
        frame.add(blankRoundedLetterPanel);

        // 显示 JFrame
        frame.setVisible(true);
    }
}
