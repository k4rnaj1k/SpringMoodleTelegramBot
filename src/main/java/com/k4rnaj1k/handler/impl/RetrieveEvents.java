package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
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
        if (Objects.equals(message, "/upcoming")) {
            Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
            List<Event> upcomingEvents = eventService.parseEvents(user.getToken());
            List<Event> tomorrow = upcomingEvents.stream().filter(upcomingEvent ->
                            upcomingEvent.getTimeStart().isBefore(today.plus(Duration.ofDays(1))))
                    .toList();
            List<Event> thisWeek = upcomingEvents.stream().filter(upcomingEventDTO ->
                            upcomingEventDTO.getTimeStart().isAfter(today.plus(Duration.ofDays(1))))
                    .filter(upcomingEventDTO ->
                            upcomingEventDTO.getTimeStart().isBefore(today.plus(Duration.ofDays(7))))
                    .toList();
            List<Event> afterWeek = upcomingEvents.stream().filter(upcomingEventDTO ->
                            upcomingEventDTO.getTimeStart().isAfter(today.plus(Duration.ofDays(7))))
                    .toList();
            SendMessage tomorrowMessage = TelegramUtil.createSendMessage(user.getChatId(), formatEvents(tomorrow));
            SendMessage thisWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), formatEvents(thisWeek));
            SendMessage afterWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), formatEvents(afterWeek));
            return List.of(tomorrowMessage, thisWeekMessage, afterWeekMessage);
        }
        return Collections.emptyList();
    }

    private String formatEvents(List<Event> events) {
        String res = "";
        for (Event event :
                events) {
            res = res.concat(event.getId()+" " + event.getName());
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
