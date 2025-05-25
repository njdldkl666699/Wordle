package io.njdldkl.pojo.event;


import io.njdldkl.pojo.User;

import java.util.List;

public record UserListUpdatedEvent(List<User> users,User hostUser) {

}
