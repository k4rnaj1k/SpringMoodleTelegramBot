package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
