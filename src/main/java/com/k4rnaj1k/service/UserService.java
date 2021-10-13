package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.GroupDTO;
import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.Group;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.repository.EventRepository;
import com.k4rnaj1k.repository.GroupRepository;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@EnableScheduling
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final Function<User, List<Event>> eventsFunction;
    private final Consumer<SendMessage> sendMessageConsumer;
    private final WebService webService;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;

    public UserService(UserRepository userRepository, Function<User, List<Event>> eventsFunction, Consumer<SendMessage> sendMessageConsumer, WebService webService, GroupRepository groupRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventsFunction = eventsFunction;
        this.sendMessageConsumer = sendMessageConsumer;
        this.webService = webService;
        this.groupRepository = groupRepository;
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedDelay = 18_000_000)
    public void parseUsersTasks() {
        List<User> users = getUsers();
        users.forEach(user ->
        {
            if (shouldParse(user.getGroups())) {
                List<Event> events = eventsFunction.apply(user);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                events.forEach(event ->
                        {
                            if (event.getTimeStart().isBefore(Instant.now().plus(7, ChronoUnit.DAYS))) {
                                event.getGroups().forEach(group ->
                                        group.getUsers().forEach(groupUser -> {
                                            SendMessage sendMessage = TelegramUtil.createSendMessage(user.getChatId(),
                                                    simpleDateFormat.format(Date.from(event.getTimeStart())) + " " + event.getName() + " " + event.getModuleName());
                                            sendMessageConsumer.accept(sendMessage);
                                        })
                                );
                            }
                        }
                );
            }

        });
    }

    private boolean shouldParse(List<Group> groups) {
//        return groups.stream().anyMatch(group -> group.getUpdatedAt().isBefore(Instant.now().minus(Duration.ofHours(4))));
        return true;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean loadUser(LoginRequest loginRequest, String chatId) {
        if (userRepository.findByChatId(Long.parseLong(chatId)).isPresent()) {
            User registered = userRepository.getByChatId(Long.parseLong(chatId));
            UserTokenDTO dto = webService.getToken(loginRequest.username(), loginRequest.password());
            registered.setToken(dto.token());
            registered.setUserId(webService.getUserId(registered.getToken()));
            userRepository.save(registered);
            Thread t = new Thread(() -> loadUsersFields(registered));
            t.start();
            registered.setState(State.LOGGED_IN);
            userRepository.save(registered);
            return true;
        } else
            return false;
    }

    private void loadUsersFields(User user) {
        List<GroupDTO> userGroups = webService.getGroups(user.getToken());
        for (GroupDTO groupDTO : userGroups) {
            Group group = groupRepository.findById(groupDTO.id())
                    .orElse(groupRepository.save(new Group(groupDTO.id(), groupDTO.name())));
            group.addUser(user);
            groupRepository.save(group);
        }
        userRepository.save(user);
//        List<CourseDTO> userCourses = webService.getCourses(user.getToken(), user.getUserId());
//        userCourses.forEach(courseDTO -> {
//            courseRepository
//        });
    }
}
