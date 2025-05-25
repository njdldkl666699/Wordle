package io.njdldkl.pojo.event;

import io.njdldkl.pojo.User;
import io.njdldkl.pojo.Word;

public record GameOverEvent(Word answer, User winner, long winnerDuration) {
}
