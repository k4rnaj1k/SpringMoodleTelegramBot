package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.UsersEvent;
import com.k4rnaj1k.model.UsersEventPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersEventRepository extends JpaRepository<UsersEvent, UsersEventPK> {
}
