package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.model.*;
import com.k4rnaj1k.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
            Event event = eventRepository.findByEventId(upcomingEvent.id()).orElse(
                    eventRepository.save(
                            new Event(upcomingEvent.id(),
                                    upcomingEvent.moduleName(),
                                    upcomingEvent.name(),
                                    upcomingEvent.timeStart())
                    )
            );
            if (upcomingEvent.groupid() != null && groupRepository.existsById(upcomingEvent.groupid())) {
                Group eventsGroup = groupRepository.findById(upcomingEvent.groupid()).orElseThrow(RuntimeException::new);
                event.addGroup(eventsGroup);
                eventsGroup.getUsers().forEach(groupUser -> {
                    if (!usersEventRepository.existsByEventAndUser(event, groupUser)) {
                        UsersEvent usersEvent = new UsersEvent(groupUser, event);
                        event.addUsersEvent(usersEvent);
                        usersEventRepository.save(usersEvent);
                    }
                });
                eventsGroup.setUpdatedAt(Instant.now());
                eventRepository.save(event);
                events.add(event);
            }


            if (upcomingEvent.course() != null && courseRepository.existsById(upcomingEvent.course().id())) {
                Course eventsCourse = courseRepository.getById(upcomingEvent.course().id());
                List<User> users = eventsCourse.getUsers();
                for (User courseUser :
                        users) {
                    if (!usersEventRepository.existsByEventAndUser(event, courseUser)) {
                        UsersEvent usersEvent = new UsersEvent(courseUser, event);
                        event.addUsersEvent(usersEvent);
                        usersEventRepository.save(usersEvent);
                    }
                }
                eventsCourse.setUpdatedAt(Instant.now());
                courseRepository.save(eventsCourse);
                eventRepository.save(event);


                events.add(event);
            }
        }
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
