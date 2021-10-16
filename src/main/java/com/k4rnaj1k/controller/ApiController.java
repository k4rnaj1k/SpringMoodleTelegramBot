package com.k4rnaj1k.controller;

import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
public class ApiController {
    private final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("submit")
    public ResponseEntity<Object> submit(@RequestBody LoginRequest loginRequest, @RequestParam("chat_id") String chatId) {
        if (userService.loadUser(loginRequest, chatId)) {
            userService.loadUsersFields(chatId);
            log.info("Successfully loaded user's fields {}.", chatId);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.error("Encountered an error during process of fields retrieval chatId = {}.", chatId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
