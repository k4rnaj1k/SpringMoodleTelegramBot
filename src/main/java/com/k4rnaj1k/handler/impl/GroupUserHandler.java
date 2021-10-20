package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.GroupHandler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.UserChat;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class GroupUserHandler implements GroupHandler {

    @Override
    @Transactional
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserChat userChat, Update update) {
        List<PartialBotApiMethod<? extends Serializable>> result = new ArrayList<>();
        if (update.getMyChatMember().getNewChatMember().getStatus().equals("member")) {
            SendMessage message = TelegramUtil.createSendMessage(userChat.getChatId(), "This chat now receives @" + update.getMyChatMember().getFrom().getUserName() + " 's updates.");
            result.add(message);
        }
        userChat.setState(State.CHAT_LOGGED_IN);
        return result;
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
