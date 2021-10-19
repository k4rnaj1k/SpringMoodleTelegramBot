package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.GroupHandler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.UserChat;
import com.k4rnaj1k.service.UserService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

@Component
public class GroupUserHandler implements GroupHandler {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserChat userChat, Update update) {
        SendMessage message = TelegramUtil.createSendMessage(userChat.getChatId(), "This group is now subscribed to upcoming events.");
        return List.of(message);
    }

    @Override
    public State operatedBotState() {
        return State.CONNECTED;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return null;
    }
}
