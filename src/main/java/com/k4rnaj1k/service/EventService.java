package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.Group;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UsersEvent;
import com.k4rnaj1k.repository.EventRepository;
import com.k4rnaj1k.repository.GroupRepository;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.repository.UsersEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
@Transactional
public class EventService {
    private final WebService webService;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final UsersEventRepository usersEventRepository;
    private final UserRepository userRepository;

    public EventService(WebService webService, EventRepository eventRepository, GroupRepository groupRepository, UsersEventRepository usersEventRepository, UserRepository userRepository) {
        this.webService = webService;
        this.eventRepository = eventRepository;
        this.groupRepository = groupRepository;
        this.usersEventRepository = usersEventRepository;
        this.userRepository = userRepository;
    }

    @Bean
    public Function<User, List<Event>> eventsSupplier() {
//        return (this::getEvents);
        return (this::parseEvents);
    }

    private List<Event> getEvents(User user) {
        return eventRepository.findAllByUser(user);
    }

    public List<Event> parseEvents(User user) {
        user = userRepository.findById(user.getId()).orElseThrow();
        List<UpcomingEventDTO> upcomingEvents = webService.loadEvents(user.getToken());
        List<Event> events = new ArrayList<>();
        for (UpcomingEventDTO upcomingEvent :
                upcomingEvents) {
            if (upcomingEvent.groupid() != null && groupRepository.existsById(upcomingEvent.groupid())) {
                Group eventsGroup = groupRepository.getById(upcomingEvent.groupid());
                Event event = eventRepository.findById(upcomingEvent.id()).orElse(
                        eventRepository
                                .save(
                                        new Event(upcomingEvent.id(),
                                                upcomingEvent.moduleName(),
                                                upcomingEvent.name(),
                                                List.of(eventsGroup), upcomingEvent.timeStart())
                                )
                );
                eventsGroup.getUsers().forEach(groupUser -> {
                    UsersEvent usersEvent = new UsersEvent(groupUser, event);
                    usersEventRepository.save(usersEvent);
                });
                events.add(event);
            }
        }
        return events;
    }
}
