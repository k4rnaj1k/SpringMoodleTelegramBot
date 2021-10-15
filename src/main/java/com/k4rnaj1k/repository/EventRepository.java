package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.User;
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
        return findAllByTimeStartAfterAndTimeStartBeforeAndUsersEvents_User(after, before, user);
    }

    List<Event> findAllByTimeStartAfterAndTimeStartBeforeAndUsersEvents_User(Instant after, Instant before, User user);

    List<Event> findAllByTimeStartAfterAndTimeStartBefore(Instant after, Instant before);

    default List<Event> findAllAfter(Instant after, User user){
        return findAllByTimeStartAfterAndUsersEvents_User(after, user);
    }

    List<Event> findAllByTimeStartAfterAndUsersEvents_User(Instant after, User user);
}
