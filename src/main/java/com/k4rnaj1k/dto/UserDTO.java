package com.k4rnaj1k.dto;

import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UserChat;

import java.util.List;

public record UserDTO(Long chatId ,List<Long> userChatIds) {
    public static UserDTO fromUser(User user){
        List<Long> userChatIds = user.getUserChats().stream().map(UserChat::getChatId).toList();
        return new UserDTO(user.getChatId(), userChatIds);
    }
}
