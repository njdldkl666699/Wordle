package io.njdldkl.view.frame;

import io.njdldkl.pojo.Word;

public interface GameOverDialogHandler {

    void showWinGameOverDialog(Word answer);

    void showLoseGameOverDialog(Word answer);
}
