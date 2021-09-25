package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.repository.EventRepository;
import com.k4rnaj1k.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


    public List<Event> getEvents(String token) {
        List<UpcomingEventDTO> upcomingEvents = webService.getEvents(token);
        List<Event> events = new ArrayList<>();
        for (UpcomingEventDTO upcomingEvent :
                upcomingEvents) {
            Event event = eventRepository.findById(upcomingEvent.id()).orElse(eventRepository
                    .save(
                            new Event(upcomingEvent.id(), upcomingEvent.moduleName(), upcomingEvent.name(), upcomingEvent.course().group)))
        }
        return null;
    }
}
