package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> getAllByUpdatedAtAfter(Instant after);
}
