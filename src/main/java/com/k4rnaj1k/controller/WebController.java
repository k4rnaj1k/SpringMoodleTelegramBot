package com.k4rnaj1k.controller;

import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.service.UserService;
import org.springframework.http.HttpStatus;
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

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(@RequestParam("chat_id") String chatId, Model model) {
        System.out.println(chatId);
        model.addAttribute("chatId", chatId);
        return "login";
    }

    @PostMapping("submit")
    public String submit(@RequestBody LoginRequest loginRequest, @RequestParam("chat_id") String chatid) {
//        if(userService.getUser())
        if (userService.loadUser(loginRequest, chatid))
            return "success";
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
