package io.njdldkl;

import io.njdldkl.dialog.EasterEggDialog;
import io.njdldkl.dialog.HelpDialog;
import io.njdldkl.frame.MenuFrame;
import io.njdldkl.frame.SinglePlayFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * 窗口管理器，用于管理应用程序的窗口切换
 */
@Slf4j
public class WindowManager {

    private static WindowManager instance;

    private MenuFrame menuFrame;
    private SinglePlayFrame singlePlayFrame;

    private EasterEggDialog easterEggDialog;
    private HelpDialog helpDialog;

    private WindowManager() {}

    public static WindowManager getInstance() {
        if (instance == null) {
            log.debug("创建窗口管理器实例");
            instance = new WindowManager();
        }
        return instance;
    }

    public void showMenuFrame() {
        if (menuFrame == null) {
            log.debug("创建主菜单窗口");
            menuFrame = new MenuFrame();
        }
        if (singlePlayFrame != null) {
            singlePlayFrame.setVisible(false);
        }
        menuFrame.setVisible(true);
    }

    public void showSinglePlayFrame() {
        if (singlePlayFrame == null) {
            log.debug("创建单人游戏窗口");
            singlePlayFrame = new SinglePlayFrame();
        }
        menuFrame.setVisible(false);
        singlePlayFrame.setVisible(true);
    }

    public void showEasterEggDialog() {
        if (easterEggDialog == null) {
            log.debug("创建彩蛋对话框");
            easterEggDialog = new EasterEggDialog(menuFrame);
        }
        easterEggDialog.setVisible(true);
    }

    public void showHelpDialog() {
        if (helpDialog == null) {
            log.debug("创建帮助对话框");
            helpDialog = new HelpDialog(menuFrame);
        }
        helpDialog.setVisible(true);
    }
}
