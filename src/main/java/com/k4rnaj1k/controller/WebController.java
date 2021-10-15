package com.k4rnaj1k.controller;

import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class WebController {
    private final UserService userService;
    private final static Logger log = LoggerFactory.getLogger(WebController.class);

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam("chat_id") String chatId, Model model) {
        log.info("Login attempt with chatId {}", chatId);
        model.addAttribute("chatId", chatId);
        return "login";
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

    @GetMapping("success")
    public String success(){
        return "success";
    }
}
