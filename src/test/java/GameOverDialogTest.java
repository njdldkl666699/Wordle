import io.njdldkl.pojo.Word;
import io.njdldkl.view.dialog.GameOverDialog;

public class GameOverDialogTest {

    public static void main(String[] args) {

        // 创建 GameOverDialog 实例
        GameOverDialog gameOverDialog = new GameOverDialog(null);
        gameOverDialog.setTitle("游戏结束");
        gameOverDialog.setWord(new Word("text","n. 文本，文字"));

        // 显示对话框
        gameOverDialog.setVisible(true);
    }
}
