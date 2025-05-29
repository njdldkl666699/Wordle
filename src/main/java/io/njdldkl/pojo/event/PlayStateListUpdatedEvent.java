package io.njdldkl.pojo.event;

import io.njdldkl.pojo.PlayState;

import java.util.Map;
import java.util.UUID;

public record PlayStateListUpdatedEvent(Map<UUID, PlayState> playStateList) {
}
