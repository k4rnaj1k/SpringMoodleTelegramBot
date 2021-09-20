package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.service.TokenService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class RegisterUser implements Handler {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public RegisterUser(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return register(user, message);
    }

    private List<PartialBotApiMethod<? extends Serializable>> register(User user, String message) {
        SendMessage sendMessage;
        try {
            String username = message.split(" ")[0];
            String password = message.split(" ")[1];
            UserTokenDTO dto = tokenService.getToken(username, password);
            if(dto.token()==null)
                throw new IllegalArgumentException();
            user.setToken(dto.token());
            user.setState(State.LOGGED_IN);
            userRepository.save(user);
            sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Successfully retrieved user's token");
        }catch (Exception e){
            sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Couldn't retrieve user's token. Make sure you've written the _username_ and _password_ right.");
        }
        return List.of(sendMessage);
    }

    @Override
    public State operatedBotState() {
        return State.START;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
