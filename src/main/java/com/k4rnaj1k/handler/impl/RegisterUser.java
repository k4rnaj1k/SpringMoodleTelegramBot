package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.dto.GroupDTO;
import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.Group;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.repository.GroupRepository;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.service.WebService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class RegisterUser implements Handler {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final WebService webService;

    public RegisterUser(UserRepository userRepository, GroupRepository groupRepository, WebService webService) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.webService = webService;
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
            UserTokenDTO dto = webService.getToken(username, password);
            if (dto.token() == null)
                throw new IllegalArgumentException();
            user.setToken(dto.token());
            user.setState(State.LOGGED_IN);
            userRepository.save(user);
            List<GroupDTO> userGroups = webService.getGroups(user.getToken());
            userGroups.forEach(groupDTO -> {
                Group group = groupRepository.findById(groupDTO.id())
                        .orElse(groupRepository.save(new Group(groupDTO.id(), groupDTO.name())));
                group.addUser(user);
            });
            sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Successfully retrieved user's token and loaded user's groups\\.");
        } catch (Exception e) {
            sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Couldn't retrieve user's token. Make sure you've written the _username_ and _password_ right\\.");
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
