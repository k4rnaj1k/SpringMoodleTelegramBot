package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import com.k4rnaj1k.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class RetrieveEvents implements Handler {

    private final EventService eventService;

    public RetrieveEvents(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return retrieveEvents(user, message);
    }

    private List<PartialBotApiMethod<? extends Serializable>> retrieveEvents(User user, String message) {
        List<PartialBotApiMethod<? extends Serializable>> result = new ArrayList<>();
        if (Objects.equals(message, "/upcoming")) {
            log.info("User issued upcoming command.");
            SendMessage tomorrowMessage = TelegramUtil.createSendMessage(user.getChatId(), "Tomorrow" + "\n" + formatEvents(eventService.getTomorrow(user), false));
            SendMessage thisWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), "This week" + "\n" + formatEvents(eventService.getThisWeek(user), true));
            SendMessage afterWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), "After this week" + "\n" + formatEvents(eventService.getAfterWeek(user), true));
            return List.of(tomorrowMessage, thisWeekMessage, afterWeekMessage);
        }
        return result;
    }

    private String formatEvents(List<Event> events, boolean afterTomorrow) {
        String res = "";
        SimpleDateFormat tomorrowFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat weekFormat = new SimpleDateFormat("dd.MM");
        for (Event event :
                events) {
            if (!afterTomorrow)
                res = res.concat(event.getId() + " " + event.getName() +
                        tomorrowFormat.format(Date.from(event.getTimeStart())) + "\n");
            else {
                res = res.concat(event.getId() + " " + event.getName() +
                        weekFormat.format(Date.from(event.getTimeStart())) + "\n");
            }
        }
        return res;
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
