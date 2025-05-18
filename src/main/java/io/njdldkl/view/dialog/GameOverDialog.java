package io.njdldkl.view.dialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;

    public GameOverDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
    }

    private void onOK() {
        // 在此处添加您的代码
        dispose();
    }
}
