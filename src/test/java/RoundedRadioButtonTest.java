import io.njdldkl.view.component.RoundedRadioButton;

import javax.swing.*;
import java.awt.*;

public class RoundedRadioButtonTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("圆角单选按钮示例");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            
            // 创建按钮组
            ButtonGroup group = new ButtonGroup();
            
            // 创建几个圆角单选按钮
            RoundedRadioButton option1 = new RoundedRadioButton(20, Color.LIGHT_GRAY, Color.BLUE);
            option1.setText("选项一");
            RoundedRadioButton option2 = new RoundedRadioButton(20, Color.LIGHT_GRAY, Color.GREEN);
            option2.setText("选项二");
            RoundedRadioButton option3 = new RoundedRadioButton(20, Color.LIGHT_GRAY, Color.RED);
            option3.setText("选项三");
            
            // 设置尺寸
            Dimension buttonSize = new Dimension(120, 40);
            option1.setPreferredSize(buttonSize);
            option2.setPreferredSize(buttonSize);
            option3.setPreferredSize(buttonSize);
            
            // 添加到按钮组
            group.add(option1);
            group.add(option2);
            group.add(option3);
            
            // 添加到面板
            frame.add(option1);
            frame.add(option2);
            frame.add(option3);
            
            // 默认选择第一个
            option1.setSelected(true);
            
            frame.setVisible(true);
        });
    }
}
