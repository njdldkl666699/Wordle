package io.njdldkl.frame;

import io.njdldkl.constant.DimensionConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BaseFrame extends JFrame {

    public BaseFrame() {
        setTitle("Wordle");
        setIconImage(Toolkit.getDefaultToolkit().getImage(BaseFrame.class.getResource("/icon/wordle.png")));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMaximumSize(DimensionConstant.FRAME_MAXIMUM);
        setMinimumSize(DimensionConstant.FRAME_MINIMUM);
        setPreferredSize(DimensionConstant.FRAME_PREFERRED);
    }
}
