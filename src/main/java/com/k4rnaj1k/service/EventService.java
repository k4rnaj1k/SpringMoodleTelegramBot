package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.repository.EventRepository;
import com.k4rnaj1k.repository.GroupRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class EventService {
    private final WebService webService;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;

    public EventService(WebService webService, EventRepository eventRepository, GroupRepository groupRepository) {
        this.webService = webService;
        this.eventRepository = eventRepository;
        this.groupRepository = groupRepository;
    }

    @Bean
    public Function<String, List<Event>> eventsSupplier() {
        return (this::parseEvents);
    }

    public List<Event> parseEvents(String token) {
        List<UpcomingEventDTO> upcomingEvents = webService.getEvents(token);
        List<Event> events = new ArrayList<>();
        for (UpcomingEventDTO upcomingEvent :
                upcomingEvents) {
            if (upcomingEvent.groupid() != null) {
                Event event = eventRepository.findById(upcomingEvent.id()).orElse(eventRepository
                        .save(
                                new Event(upcomingEvent.id(), upcomingEvent.moduleName(), upcomingEvent.name(), List.of(groupRepository.findById(upcomingEvent.groupid()).orElseThrow()))));
                events.add(event);
            }
        }
        return events;
    }
}
