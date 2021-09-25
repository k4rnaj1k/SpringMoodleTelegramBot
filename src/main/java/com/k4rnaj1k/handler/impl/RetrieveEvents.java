package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

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
            List<UpcomingEventDTO> upcomingEvents = eventService.getEvents(user.getToken());
            List<UpcomingEventDTO> tomorrow = upcomingEvents.stream().filter(upcomingEvent ->
                            upcomingEvent.timeStart().isBefore(today.plus(Duration.ofDays(1))))
                    .toList();
            List<UpcomingEventDTO> thisWeek = upcomingEvents.stream().filter(upcomingEventDTO ->
                            upcomingEventDTO.timeStart().isAfter(today.plus(Duration.ofDays(1))))
                    .filter(upcomingEventDTO ->
                            upcomingEventDTO.timeStart().isBefore(today.plus(Duration.ofDays(7))))
                    .toList();
            List<UpcomingEventDTO> afterWeek = upcomingEvents.stream().filter(upcomingEventDTO ->
                            upcomingEventDTO.timeStart().isAfter(today.plus(Duration.ofDays(7))))
                    .toList();
            tomorrow.forEach(System.out::println);
            thisWeek.forEach(System.out::println);
            afterWeek.forEach(System.out::println);
        }
        return Collections.emptyList();
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
