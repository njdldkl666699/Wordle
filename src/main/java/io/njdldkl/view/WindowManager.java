package io.njdldkl.view;

import io.njdldkl.pojo.Word;
import io.njdldkl.view.dialog.EasterEggDialog;
import io.njdldkl.view.dialog.GameOverDialog;
import io.njdldkl.view.dialog.HelpDialog;
import io.njdldkl.view.frame.MenuFrame;
import io.njdldkl.view.frame.MultiPlayFrame;
import io.njdldkl.view.frame.SinglePlayFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * 窗口管理器，用于管理应用程序的窗口切换
 */
@Slf4j
public class WindowManager {

    private static WindowManager instance;

    private MenuFrame menuFrame;
    private SinglePlayFrame singlePlayFrame;
    private MultiPlayFrame multiPlayFrame;

    private EasterEggDialog easterEggDialog;
    private HelpDialog helpDialog;
    private GameOverDialog gameOverDialog;

    private WindowManager() {}

    public static WindowManager getInstance() {
        if (instance == null) {
            log.info("创建窗口管理器实例");
            instance = new WindowManager();
        }
        return instance;
    }

    public void showMenuFrame() {
        if (menuFrame == null) {
            log.info("创建主菜单窗口");
            menuFrame = new MenuFrame();
        }
        if (singlePlayFrame != null) {
            singlePlayFrame.setVisible(false);
        }
        if (multiPlayFrame != null) {
            multiPlayFrame.setVisible(false);
        }
        menuFrame.setVisible(true);
    }

    public void showSinglePlayFrame() {
        if (singlePlayFrame == null) {
            log.info("创建单人游戏窗口");
            singlePlayFrame = new SinglePlayFrame();
        }
        menuFrame.setVisible(false);
        singlePlayFrame.startGame(menuFrame.getUser());
        singlePlayFrame.setVisible(true);
    }

    public void showMultiPlayFrame() {
        if (multiPlayFrame == null) {
            log.info("创建多人游戏窗口");
            multiPlayFrame = new MultiPlayFrame();
        }
        menuFrame.setVisible(false);
        multiPlayFrame.setVisible(true);
    }

    public void showEasterEggDialog() {
        if (easterEggDialog == null) {
            log.info("创建彩蛋对话框");
            easterEggDialog = new EasterEggDialog(menuFrame);
        }
        easterEggDialog.setVisible(true);
    }

    public void showHelpDialog() {
        if (helpDialog == null) {
            log.info("创建帮助对话框");
            helpDialog = new HelpDialog(menuFrame);
        }
        helpDialog.setVisible(true);
    }

    public void showGameOverDialog(String title, Word word, JFrame parent) {
        if (gameOverDialog == null) {
            log.info("创建游戏结束对话框");
            gameOverDialog = new GameOverDialog(singlePlayFrame);
            gameOverDialog.addNewGameButtonListener(e->{
                // 单人模式下，返回主菜单即可
                // 多人模式下，返回主菜单，逻辑待定
                gameOverDialog.setVisible(false);
                parent.setVisible(false);
                menuFrame.setVisible(true);
            });
        }
        gameOverDialog.setTitle(title);
        gameOverDialog.setWord(word);
        gameOverDialog.setVisible(true);
    }
}
