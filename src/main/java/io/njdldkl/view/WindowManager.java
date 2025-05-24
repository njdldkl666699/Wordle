package io.njdldkl.view;

import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.IpRoomIdUtils;
import io.njdldkl.view.dialog.BackHomeDialog;
import io.njdldkl.view.dialog.EasterEggDialog;
import io.njdldkl.view.dialog.GameOverDialog;
import io.njdldkl.view.dialog.HelpDialog;
import io.njdldkl.view.frame.MenuFrame;
import io.njdldkl.view.frame.MultiPlayFrame;
import io.njdldkl.view.frame.SinglePlayFrame;
import io.njdldkl.view.frame.WaitingRoomFrame;
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
    private WaitingRoomFrame createRoomFrame;
    private WaitingRoomFrame joinRoomFrame;

    private EasterEggDialog easterEggDialog;
    private HelpDialog helpDialog;
    private GameOverDialog gameOverDialog;
    private BackHomeDialog backHomeDialog;

    private WindowManager() {
    }

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
        if (createRoomFrame != null) {
            createRoomFrame.setVisible(false);
        }
        if (joinRoomFrame != null) {
            joinRoomFrame.setVisible(false);
        }
        menuFrame.setVisible(true);
    }

    public void showSinglePlayFrame() {
        if (singlePlayFrame == null) {
            log.info("创建单人游戏窗口");
            singlePlayFrame = new SinglePlayFrame(menuFrame.getUser());
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

        // 获取多人游戏服务、字母数量和当前用户
        MultiPlayService playService;
        int letterCount;
        User user;

        // 判断当前使用的是哪个房间
        WaitingRoomFrame activeRoomFrame = null;

        // 确定当前活动的房间框架
        if (createRoomFrame != null && createRoomFrame.isVisible()) {
            activeRoomFrame = createRoomFrame;
            log.info("从创建房间进入游戏");
        } else if (joinRoomFrame != null && joinRoomFrame.isVisible()) {
            activeRoomFrame = joinRoomFrame;
            log.info("从加入房间进入游戏");
        } else {
            // 如果都不可见，则查看哪个房间最后被操作过
            if (createRoomFrame != null && createRoomFrame.getMultiPlayService() != null) {
                activeRoomFrame = createRoomFrame;
                log.info("从创建房间恢复游戏");
            } else if (joinRoomFrame != null && joinRoomFrame.getMultiPlayService() != null) {
                activeRoomFrame = joinRoomFrame;
                log.info("从加入房间恢复游戏");
            }
        }

        if (activeRoomFrame == null) {
            throw new NullPointerException("没有找到多人游戏服务");
        }

        // 获取活动房间的服务和设置
        playService = activeRoomFrame.getMultiPlayService();
        letterCount = activeRoomFrame.getLetterCount();
        user = activeRoomFrame.getCurrentUser();

        // 隐藏房间框架
        if (createRoomFrame != null) {
            createRoomFrame.setVisible(false);
        }
        if (joinRoomFrame != null) {
            joinRoomFrame.setVisible(false);
        }
        menuFrame.setVisible(false);

        // 显示游戏框架并开始游戏
        multiPlayFrame.setVisible(true);
        multiPlayFrame.startGame(playService, user, letterCount);
    }

    public void showCreateRoomFrame() {
        if (createRoomFrame == null) {
            log.info("创建 创建房间窗口");
            createRoomFrame = new WaitingRoomFrame();
        }
        menuFrame.setVisible(false);
        createRoomFrame.updateUI(menuFrame.getUser(), true,
                IpRoomIdUtils.ipToRoomId(IpRoomIdUtils.getLocalHostAddress()));
        createRoomFrame.setVisible(true);
    }

    public void showJoinRoomFrame() {
        if (joinRoomFrame == null) {
            log.info("创建 加入房间窗口");
            joinRoomFrame = new WaitingRoomFrame();
        }
        menuFrame.setVisible(false);
        joinRoomFrame.updateUI(menuFrame.getUser(), false, menuFrame.getRoomId());
        joinRoomFrame.setVisible(true);
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
            gameOverDialog.addBackHomeButtonListener(e -> {
                // 单人模式下，返回主菜单即可
                // 多人模式下，返回主菜单，断开连接
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
