package io.njdldkl.view.frame;

import com.google.common.eventbus.Subscribe;
import io.njdldkl.constant.IntegerConstant;
import io.njdldkl.pojo.PlayStateVO;
import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;
import io.njdldkl.pojo.event.GameOverEvent;
import io.njdldkl.pojo.event.PlayStateListShowEvent;
import io.njdldkl.service.impl.MultiPlayService;
import io.njdldkl.util.ComponentUtils;
import io.njdldkl.view.WindowManager;
import io.njdldkl.view.component.KeyboardPanel;
import io.njdldkl.view.component.PlayStatePanel;
import io.njdldkl.view.component.RoundedButton;
import io.njdldkl.view.dialog.BackHomeDialog;
import io.njdldkl.view.dialog.GameOverDialog;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class MultiPlayFrame extends BaseFrame implements GameOverDialogHandler {

    private JPanel contentPane;
    private final PlayFrameHelper playFrameHelper;
    private MultiPlayService multiPlayService;

    private RoundedButton homeButton;
    private RoundedButton giveUpButton;

    private JScrollPane guessScrollPane;
    private JPanel guessPane;
    private KeyboardPanel keyboardPane;
    private JScrollPane playStatesScrollPane;
    private JPanel playStatesPane;

    private User currentUser;

    public MultiPlayFrame() {
        setContentPane(contentPane);

        // 游戏状态列表面板
        playStatesPane = new JPanel();
        playStatesPane.setLayout(new BoxLayout(playStatesPane, BoxLayout.X_AXIS));
        playStatesPane.setBackground(Color.WHITE);
        playStatesPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        playStatesScrollPane.setViewportView(playStatesPane);

        playFrameHelper = PlayFrameHelper.builder()
                .frame(this)
                .gameOverDialogHandler(this)
                .guessScrollPane(guessScrollPane)
                .guessPane(guessPane)
                .keyboardPane(keyboardPane)
                .build();
        playFrameHelper.initUI();

        pack();
        ComponentUtils.setCenterWindowOnScreen(this);

        playFrameHelper.setupListeners();
        // 返回主菜单按钮
        homeButton.addActionListener(e -> {
            BackHomeDialog backHomeDialog = createBackHomeDialog();
            backHomeDialog.setVisible(true);
        });
        // 认输按钮
        giveUpButton.addActionListener(e -> handleGiveUpButtonPressed());
    }

    /**
     * <p>开启新的一局游戏</p>
     * 多人游戏下，从waitingRoomFrame传入的字母数量
     */
    public void startGame(MultiPlayService playService, User currentUser, int letterCount) {
        if (multiPlayService != null) {
            // 注销原来的事件监听器
            multiPlayService.unregisterEventListener(this);
        }

        // 更新游戏
        multiPlayService = playService;
        // 注册为事件监听器
        multiPlayService.registerEventListener(this);
        playFrameHelper.setPlayService(playService);

        this.currentUser = currentUser;
        // 更新面板
        playFrameHelper.updateGuessPane(letterCount);

        // 主动请求更新一次游戏状态面板
        multiPlayService.requestPlayStatesUpdate();
    }

    private void handleGiveUpButtonPressed() {
        // 先获取答案
        Word answer = multiPlayService.getAnswer();

        // 创建游戏结束对话框
        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.setTitle("游戏失败！");
        gameOverDialog.setWord(answer);

        // 为返回主菜单按钮添加监听器
        gameOverDialog.addBackHomeButtonListener(e -> handleBackHomeFromGameOver(gameOverDialog));

        // 显示游戏结束对话框
        gameOverDialog.setVisible(true);
    }

    private BackHomeDialog createBackHomeDialog() {
        BackHomeDialog backHomeDialog = new BackHomeDialog(this);
        backHomeDialog.addConfirmButtonListener(e -> {
            // 处理网络断开
            if (multiPlayService != null) {
                // 通知服务离开房间
                multiPlayService.leaveRoom();
                log.info("用户离开房间并返回主菜单");
            }
            backHomeDialog.setVisible(false);
            // 返回主菜单
            WindowManager.getInstance().showMenuFrame();
        });
        return backHomeDialog;
    }

    private void createUIComponents() {
        homeButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        giveUpButton = new RoundedButton(IntegerConstant.SMOOTH_RADIUS);
        keyboardPane = new KeyboardPanel();
        guessPane = new JPanel();
        guessPane.setBackground(Color.WHITE);
        guessPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Subscribe
    public void onPlayListShow(PlayStateListShowEvent event) {
        SwingUtilities.invokeLater(()->{
            // 清空当前的游戏状态面板
            playStatesPane.removeAll();

            for (PlayStateVO playStateVO : event.playStateVOList()) {
                // 创建一个玩家的游戏状态面板
                PlayStatePanel playStatePanel = new PlayStatePanel(
                        playStateVO.getAvatar(), playStateVO.getName(),
                        playStateVO.getCorrectCount(), playStateVO.getWrongPositionCount());

                playStatesPane.add(playStatePanel);
                playStatesPane.add(Box.createHorizontalStrut(10));
            }

            playStatesPane.revalidate();
            playStatesPane.repaint();
            playStatesScrollPane.revalidate();
            playStatesScrollPane.repaint();
        });
    }

    // 标记是否已经显示了游戏结束对话框
    private volatile boolean gameOverDialogShown = false;

    /**
     * <p>处理游戏结束事件</p>
     * 一个玩家胜利，服务器发送游戏结束响应<br>
     * 其他玩家接收事件后，弹出失败提示框
     */
    @Subscribe
    public void onGameOverEvent(GameOverEvent gameOverEvent) {
        // 如果已经显示了游戏结束对话框，则不再显示
        if (gameOverDialogShown) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            // 设置标志，防止其他对话框显示
            gameOverDialogShown = true;

            // 获取事件中的信息
            Word answer = gameOverEvent.answer();
            User winner = gameOverEvent.winner();
            long winnerDuration = gameOverEvent.winnerDuration();

            // 创建游戏结束对话框
            GameOverDialog gameOverDialog = new GameOverDialog(this);

            // 判断当前用户是否为胜利者
            boolean isCurrentUserWinner = winner.getId().equals(currentUser.getId());

            if (isCurrentUserWinner) {
                gameOverDialog.setTitle("恭喜你获胜！");
            } else {
                // 当前用户失败，显示胜利者信息
                gameOverDialog.setTitle("游戏结束");

                // 创建一个包含胜利者信息的面板
                JPanel winnerInfoPanel = createWinnerInfoPanel(winner.getName(), winnerDuration);

                // 设置胜利者信息到对话框
                gameOverDialog.addExtraInfoPanel(winnerInfoPanel);
            }

            // 设置答案
            gameOverDialog.setWord(answer);

            // 为返回主菜单按钮添加监听器
            gameOverDialog.addBackHomeButtonListener(e -> handleBackHomeFromGameOver(gameOverDialog));

            // 显示游戏结束对话框
            gameOverDialog.setVisible(true);
        });
    }

    private static JPanel createWinnerInfoPanel(String winnerName, long winnerDuration) {
        JPanel winnerInfoPanel = new JPanel();
        winnerInfoPanel.setLayout(new BoxLayout(winnerInfoPanel, BoxLayout.Y_AXIS));
        winnerInfoPanel.setOpaque(false);
        winnerInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 添加胜利者ID标签
        JLabel winnerLabel = new JLabel("获胜者: " + winnerName);
        winnerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        winnerInfoPanel.add(winnerLabel);

        // 添加用时标签
        String formattedTime = String.format("%.1f", winnerDuration / 1000.0);
        JLabel timeLabel = new JLabel("用时: " + formattedTime + "秒");
        timeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        winnerInfoPanel.add(timeLabel);
        return winnerInfoPanel;
    }

    @Override
    public void showWinGameOverDialog(Word answer) {
        // 在多人游戏中，胜利对话框在GameOverEvent中处理
    }

    @Override
    public void showLoseGameOverDialog(Word answer) {
        // 如果已经显示了游戏结束对话框，则不再显示
        if (gameOverDialogShown) {
            return;
        }

        log.info("玩家失败，创建游戏结束失败对话框");

        // 设置标志，防止其他对话框显示
        gameOverDialogShown = true;

        GameOverDialog gameOverDialog = new GameOverDialog(this);
        gameOverDialog.setTitle("游戏失败！");
        gameOverDialog.setWord(answer);

        // 为返回主菜单按钮添加监听器
        gameOverDialog.addBackHomeButtonListener(e -> handleBackHomeFromGameOver(gameOverDialog));

        // 显示游戏结束对话框
        gameOverDialog.setVisible(true);
    }

    /**
     * 处理从游戏结束对话框返回主菜单
     */
    private void handleBackHomeFromGameOver(GameOverDialog gameOverDialog) {
        if (multiPlayService != null) {
            if (multiPlayService.isHost(currentUser.getId())) {
                // 如果是房主，二次确认离开房间
                BackHomeDialog backHomeDialog = createBackHomeDialog();
                backHomeDialog.addConfirmButtonListener(e -> gameOverDialog.dispose());
                backHomeDialog.setVisible(true);
            } else {
                // 非房主直接离开房间
                multiPlayService.leaveRoom();
            }
        }

        gameOverDialog.setVisible(false);
        setVisible(false);
        // 显示主菜单
        WindowManager.getInstance().showMenuFrame();
        gameOverDialog.dispose();
    }
}
