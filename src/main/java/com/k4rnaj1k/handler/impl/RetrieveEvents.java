package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class RetrieveEvents implements Handler {

    private EventService eventService;

    public RetrieveEvents(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return retriveEvents(user, message);
    }

    private List<PartialBotApiMethod<? extends Serializable>> retriveEvents(User user, String message) {
        eventService.getEvents(user.getToken());
        return null;
    }

    @Override
    public State operatedBotState() {
        return State.LOGGED_IN;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
