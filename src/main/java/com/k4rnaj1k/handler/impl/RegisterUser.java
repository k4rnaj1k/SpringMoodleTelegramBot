package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.Handler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
@Transactional
public class RegisterUser implements Handler {
    @Value("${web.currenturl}")
    private String currentUrl;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        return register(user, message);
    }

    /* private List<PartialBotApiMethod<? extends Serializable>> register(User user, String message) {
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
                 userRepository.save(user);
                 groupRepository.save(group);
             });
             sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Successfully retrieved user's token and loaded user's groups.");
         } catch (Exception e) {
             sendMessage = TelegramUtil.createSendMessage(user.getChatId(), "Couldn't retrieve user's token. Make sure you've written the _username_ and _password_ right.");
         }
         return List.of(sendMessage);
     }*/
    private List<PartialBotApiMethod<? extends Serializable>> register(User user, String message) {
        if (message.equals("/login")) {
            SendMessage result = TelegramUtil.createSendMessageWithUrl(user.getChatId(), "[register](" + currentUrl + "/login?chat_id=" + user.getChatId().toString() + ")");
            return List.of(result);
        } else
            return Collections.emptyList();
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
