package io.njdldkl.view.frame;

import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.service.impl.SinglePlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class MultiPlayFrame extends BaseFrame{

    private JPanel contentPane;
    private final PlayFrameHelper playFrameHelper;

    private RoundedButton homeButton;
    private RoundedButton giveUpButton;

    private JScrollPane guessScrollPane;
    private JPanel guessPane;
    private KeyboardPanel keyboardPane;

    private JPanel usersPane;

    public MultiPlayFrame(){
        setContentPane(contentPane);

        playFrameHelper = PlayFrameHelper.builder()
                .frame(this)
                // TODO 多用户对战服务
                .playService(new SinglePlayService())
                .homeButton(homeButton)
                .giveUpButton(giveUpButton)
                .guessScrollPane(guessScrollPane)
                .guessPane(guessPane)
                .keyboardPane(keyboardPane)
                .build();
        playFrameHelper.initUI();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        playFrameHelper.setListeners();
    }

    private void createUIComponents() {
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        giveUpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        keyboardPane = new KeyboardPanel();
        guessPane = new JPanel();
        guessPane.setBackground(Color.WHITE);
        guessPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
