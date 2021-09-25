package com.k4rnaj1k.service;

import com.k4rnaj1k.model.User;
import com.k4rnaj1k.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(WebClient webClient, UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public 

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
