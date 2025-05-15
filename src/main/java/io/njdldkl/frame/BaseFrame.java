package io.njdldkl.frame;

import javax.swing.*;
import java.awt.*;

public class BaseFrame extends JFrame {

    public BaseFrame() {
        setTitle("Wordle");
        setIconImage(Toolkit.getDefaultToolkit().getImage(BaseFrame.class.getResource("/icon/wordle.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMaximumSize(new Dimension(1920, 1080));
        setMinimumSize(new Dimension(960, 540));
        setPreferredSize(new Dimension(1280, 720));
    }
}
