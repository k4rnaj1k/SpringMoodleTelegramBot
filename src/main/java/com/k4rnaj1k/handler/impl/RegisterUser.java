package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
@Transactional
public class RegisterUser implements Handler {
    @Value("${web.currenturl}")
    private String currentUrl;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return register(user, message);
    }

    private List<PartialBotApiMethod<? extends Serializable>> register(User user, String message) {
        SendMessage result = switch (message) {
            case "/login" -> TelegramUtil.createSendMessageWithUrl(user.getChatId(), "[register](" + currentUrl + "/login?chat_id=" + user.getChatId().toString() + ")");
            case "/start" -> TelegramUtil.createSendMessageWithUrl(user.getChatId(), "Hello\\! This bot automatically checks user's events and notifies when there are any upcoming\\. To get the login form \\- send /login command\\.");
            default -> TelegramUtil.createSendMessage(user.getChatId(), "Message not recognized. Send /login to get the link to log into the bot.");
        };
        return List.of(result);
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
