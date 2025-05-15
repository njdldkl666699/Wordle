import io.njdldkl.component.RoundedButton;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

public class RoundedButtonTest {

    public static void main(String[] args) {
        JFrame frame = new JFrame("圆角按钮示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        RoundedButton button = new RoundedButton(20);
        button.setBackground(new Color(70, 130, 180)); // 设置背景色
        button.setForeground(Color.WHITE);             // 设置文字颜色
        button.setPreferredSize(new Dimension(120, 40)); // 按钮大小

        frame.add(button);
        frame.pack();
        frame.setVisible(true);
    }
}
