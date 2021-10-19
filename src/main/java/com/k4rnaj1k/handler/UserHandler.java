package com.k4rnaj1k.handler;

import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UserChat;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.List;

public interface UserHandler {
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update);

    State operatedBotState();

    List<String> operatedCallBackQuery();
}
