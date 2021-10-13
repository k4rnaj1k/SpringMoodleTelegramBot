package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByUsersEvents_User(User user);

    default List<Event> findAllByUser(User user) {
        return findAllByUsersEvents_User(user);
    }
}
