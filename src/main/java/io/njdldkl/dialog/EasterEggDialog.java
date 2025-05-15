package io.njdldkl.dialog;

import javax.swing.*;

// TODO 彩蛋对话框
public class EasterEggDialog extends JDialog {

    public EasterEggDialog(JFrame parent) {
        super(parent, "彩蛋", true);
    }

    public EasterEggDialog() {
        super((JFrame) null, "彩蛋", true);
    }
}
