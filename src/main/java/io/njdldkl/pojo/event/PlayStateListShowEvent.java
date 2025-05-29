package io.njdldkl.pojo.event;

import io.njdldkl.pojo.PlayStateVO;

import java.util.List;

public record PlayStateListShowEvent(List<PlayStateVO> playStateVOList) {
}
