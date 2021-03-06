package com.k4rnaj1k.controller;

import com.k4rnaj1k.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("success")
    public String success() {
        return "success";
    }
}
