package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.upcoming.UpcomingEventDTO;
import com.k4rnaj1k.model.*;
import com.k4rnaj1k.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@Service
@Transactional
@Slf4j
public class EventService {
    private final WebService webService;
    private final EventRepository eventRepository;
    private final GroupRepository groupRepository;
    private final UsersEventRepository usersEventRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public EventService(WebService webService, EventRepository eventRepository, GroupRepository groupRepository, UsersEventRepository usersEventRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.webService = webService;
        this.eventRepository = eventRepository;
        this.groupRepository = groupRepository;
        this.usersEventRepository = usersEventRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Bean
    public Function<User, List<Event>> eventsSupplier() {
        return (this::parseEvents);
    }

    private List<Event> getEvents(User user) {
        return eventRepository.findAllByUser(user);
    }

    public List<Event> parseEvents(User user) {
        log.info("Parsing events for user " + user.getChatId());
        user = userRepository.findById(user.getId()).orElseThrow();

        List<UpcomingEventDTO> upcomingEvents = webService.loadEvents(user.getToken());
        List<Event> events = new ArrayList<>();

        for (UpcomingEventDTO upcomingEvent :
                upcomingEvents) {
            Event event = eventRepository.findByEventId(upcomingEvent.id()).orElseGet(() ->
                    eventRepository.save(
                            new Event(upcomingEvent.id(),
                                    upcomingEvent.moduleName(),
                                    upcomingEvent.name().replaceAll("\s\\((.)*\\)", ""),
                                    upcomingEvent.timeStart(),
                                    upcomingEvent.url())
                    )
            );

            if (upcomingEvent.groupid() != null && groupRepository.existsById(upcomingEvent.groupid())) {
                Group eventsGroup = groupRepository.getById(upcomingEvent.groupid());
                if (!eventsGroup.equals(event.getGroup())) {
                    event.setGroup(eventsGroup);
                }
                eventsGroup.setUpdatedAt(Instant.now());
            }

            if (upcomingEvent.course() != null && courseRepository.existsById(upcomingEvent.course().id())) {
                Course eventsCourse = courseRepository.getById(upcomingEvent.course().id());
                if (!eventsCourse.equals(event.getCourse())) {
                    event.setCourse(eventsCourse);
                }
                eventsCourse.setUpdatedAt(Instant.now());
                event.setCourse(eventsCourse);
            }

            Set<User> users = event.getUsers();
            log.info("users size = {}", users.size());
            for (User eventUser :
                    users) {
                if (!usersEventRepository.existsByEventAndUser(event, eventUser)) {
                    UsersEvent usersEvent = new UsersEvent(eventUser, event);
                    event.addUsersEvent(usersEvent);
                    usersEventRepository.save(usersEvent);
                }
                log.info("{} - {}", user.getChatId(), event.getName());
            }
            events.add(event);
            eventRepository.save(event);
        }
        log.info("Successfully parsed user's events.");
        return events;
    }

    public List<Event> getTomorrow(User user) {
        return eventRepository.findAllAfterAndBefore(Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS), user);
    }

    public List<Event> getThisWeek(User user) {
        return eventRepository.findAllAfterAndBefore(Instant.now().plus(1, ChronoUnit.DAYS), Instant.now().plus(7, ChronoUnit.DAYS), user);
    }

    public List<Event> getAfterWeek(User user) {
        return eventRepository.findAllAfter(Instant.now().plus(7, ChronoUnit.DAYS), user);
    }
}
