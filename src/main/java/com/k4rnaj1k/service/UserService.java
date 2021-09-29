package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.Group;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@EnableScheduling
public class UserService {
    private final UserRepository userRepository;

    private final Function<String,List<Event>> eventsFunction;
    private final Consumer<SendMessage> sendMessageConsumer;

    public UserService(UserRepository userRepository, Function<String, List<Event>> eventsFunction, Consumer<SendMessage> sendMessageConsumer) {
        this.userRepository = userRepository;
        this.eventsFunction = eventsFunction;
        this.sendMessageConsumer = sendMessageConsumer;
    }

    @Scheduled(fixedDelay = 18_000_000L)
    public void parseUsersTasks(){
        List<User> users = getUsers();
        users.forEach(user ->
        {
            if (shouldParse(user.getGroups())) {
                List<Event> events = eventsFunction.apply(user.getToken());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                events.forEach(event ->
                        {
                            if (event.getTimeStart().isBefore(Instant.now().plus(7, ChronoUnit.DAYS))) {

                                SendMessage sendMessage = TelegramUtil.createSendMessage(user.getChatId(),
                                        simpleDateFormat.format(Date.from(event.getTimeStart())) + " " + event.getName() + " " + event.getModuleName());
                                sendMessageConsumer.accept(sendMessage);
                            }
                        }
                );
            }

        });
    }

    private boolean shouldParse(List<Group> groups){
        return groups.stream().anyMatch(group -> group.getUpdatedAt().isBefore(Instant.now().minus(Duration.ofHours(4))));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
