package com.k4rnaj1k.controller;

import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@Slf4j
public class ApiController {
    private final UserService userService;

    @Value("${admin.login}")
    private String adminLogin;
    @Value("${admin.password}")
    private String adminPass;

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

    @PostMapping("submit-admin")
    public ResponseEntity<Object> submit(@RequestBody LoginRequest loginRequest) {
        if(Objects.equals(loginRequest.username(), adminLogin) && Objects.equals(loginRequest.password(), adminPass)){
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            log.error("Someone is trying to log in as admin.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
