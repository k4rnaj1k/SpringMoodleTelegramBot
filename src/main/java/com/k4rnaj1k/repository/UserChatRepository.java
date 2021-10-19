package com.k4rnaj1k.repository;

import com.k4rnaj1k.model.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {
    Optional<UserChat> findByChatId(Long chatId);

    void deleteByChatId(Long chatId);
}
