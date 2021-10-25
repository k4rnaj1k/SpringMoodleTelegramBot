package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByUsersEvents_User(User user);

    Optional<Event> findByEventId(Long eventId);

    default List<Event> findAllByUser(User user) {
        return findAllByUsersEvents_User(user);
    }

    default List<Event> findAllAfterAndBefore(Instant after, Instant before) {
        return findAllByTimeStartAfterAndTimeStartBefore(after, before);
    }

    default List<Event> findAllAfterAndBefore(Instant after, Instant before, User user) {
        return findAllByTimeStartAfterAndTimeStartBeforeAndUsersEvents_User(after, before, user, Sort.by(Sort.Direction.ASC, "timeStart"));
    }

    List<Event> findAllByTimeStartAfterAndTimeStartBeforeAndUsersEvents_User(Instant after, Instant before, User user, Sort sort);

    List<Event> findAllByTimeStartAfterAndTimeStartBefore(Instant after, Instant before);

    default List<Event> findAllAfter(Instant after, User user) {
        return findAllByTimeStartAfterAndUsersEvents_User(after, user);
    }

    List<Event> findAllByTimeStartAfterAndUsersEvents_User(Instant after, User user);

    default List<Event> findAllByModuleNameAndAfterAndBefore(Event.ModuleName moduleName, Instant after, Instant before) {
        return findAllByModuleNameAndTimeStartAfterAndTimeStartBefore(moduleName, after, before);
    }

    default List<Event> findAllByModuleNameAndAfterAndBefore(Event.ModuleName moduleName, Event.EventType eventType, Instant after, Instant before) {
        return findAllByModuleNameAndEventTypeAndTimeStartBeforeAndTimeStartAfter(moduleName, eventType, after, before);
    }

    List<Event> findAllByModuleNameAndEventTypeAndTimeStartBeforeAndTimeStartAfter(Event.ModuleName moduleName, Event.EventType eventType, Instant after, Instant before);

    List<Event> findAllByModuleNameAndTimeStartAfterAndTimeStartBefore(Event.ModuleName moduleName, Instant after, Instant before);
}
