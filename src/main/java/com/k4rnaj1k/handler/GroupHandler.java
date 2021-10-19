package com.k4rnaj1k.handler;

import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.UserChat;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public interface GroupHandler {
    List<PartialBotApiMethod<? extends Serializable>> handle(UserChat userChat, Update update);

    State operatedBotState();

    List<String> operatedCallBackQuery();
}
