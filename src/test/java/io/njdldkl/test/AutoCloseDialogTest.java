package io.njdldkl.test;

import io.njdldkl.view.dialog.AutoCloseDialog;

import javax.swing.*;

public class AutoCloseDialogTest {

    public static void main(String[] args) {
        // 创建一个 JFrame 用于测试
        JFrame frame = new JFrame("AutoCloseDialog 测试");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        // 创建 AutoCloseDialog 实例
        AutoCloseDialog autoCloseDialog = new AutoCloseDialog(frame);
        autoCloseDialog.setText("你好，世界！");

        // 显示对话框
        frame.setVisible(true);
        autoCloseDialog.setVisible(true);
    }
}
