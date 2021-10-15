package com.k4rnaj1k.handler;

import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.io.Serializable;
import java.util.List;

public interface Handler {
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);

    State operatedBotState();

    List<String> operatedCallBackQuery();
}
