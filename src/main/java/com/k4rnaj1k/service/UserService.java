package com.k4rnaj1k.service;

import com.k4rnaj1k.dto.LoginRequest;
import com.k4rnaj1k.dto.UserTokenDTO;
import com.k4rnaj1k.dto.upcoming.CourseDTO;
import com.k4rnaj1k.dto.upcoming.GroupDTO;
import com.k4rnaj1k.model.*;
import com.k4rnaj1k.repository.CourseRepository;
import com.k4rnaj1k.repository.EventRepository;
import com.k4rnaj1k.repository.GroupRepository;
import com.k4rnaj1k.repository.UserRepository;
import com.k4rnaj1k.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@EnableScheduling
@Transactional
@Slf4j
public class UserService {

    private final Function<User, List<Event>> eventsFunction;
    private final Consumer<SendMessage> sendMessageConsumer;

    private final WebService webService;

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CourseRepository courseRepository;
    private static final Timer timer = new Timer("Notifier timer.");
    private static final Set<TimerTask> notifications = new LinkedHashSet<>();


    public UserService(UserRepository userRepository, Function<User, List<Event>> eventsFunction, Consumer<SendMessage> sendMessageConsumer, WebService webService, GroupRepository groupRepository, EventRepository eventRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.eventsFunction = eventsFunction;
        this.sendMessageConsumer = sendMessageConsumer;
        this.webService = webService;
        this.groupRepository = groupRepository;
        this.eventRepository = eventRepository;
        this.courseRepository = courseRepository;
    }

//    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 36_00_000L)
    public void checkSelfAssignment() {
        log.info("Checking assignments.");
        int notificationCount = 0;
        List<Event> events = eventRepository.findAllByModuleNameAndTimeStartAfterAndTimeStartBefore(Event.ModuleName.attendance, Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS));
        for (Event event :
                events) {
            Set<User> users = event.getUsers();
            for (User user :
                    users) {
                String message = "Here's the link to mark your attendance\n" + event.getUrl();
                TimerTask timerTask = new NotifyAboutAttendanceTask(message, user.getChatId());
                if (notifications.contains(timerTask))
                    continue;
                timer.schedule(timerTask, Date.from(event.getTimeStart()));
                notifications.add(timerTask);
                notificationCount++;
            }
        }
        log.info("Successfully scheduled " + notificationCount + " notifications.");
    }

    private class NotifyAboutAttendanceTask extends TimerTask {

        private final String message;
        private final Long chatId;

        public NotifyAboutAttendanceTask(String message, Long chatId) {
            this.message = message;
            this.chatId = chatId;
        }

        @Override
        public void run() {
            sendMessageConsumer.accept(TelegramUtil.createSendMessageWithUrl(chatId, message));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NotifyAboutAttendanceTask that = (NotifyAboutAttendanceTask) o;
            return Objects.equals(message, that.message) && Objects.equals(chatId, that.chatId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(message, chatId);
        }
    }

    @Scheduled(cron = "0 */10 8-20 * * *")
    public void checkUsersTasks() {
        log.info("checkUsersTasks - checking user's tasks.");
        List<Event> events = eventRepository.findAllAfterAndBefore(Instant.now(), Instant.now().plus(Duration.ofDays(1)));
        Map<Long, String> usersMessages = new LinkedHashMap<>();
        events.forEach(event -> event.getUsersEvents().forEach(usersEvent -> {
            if (!usersEvent.isNotified()) {
                Long chatId = usersEvent.getUser().getChatId();
                String message = DateTimeFormatter.ofPattern("hh:mm").format(LocalDateTime.ofInstant(event.getTimeStart(), ZoneId.of("Europe/Kiev"))) + " " + event.getName() + " " + event.getCourse().getShortName();
                if (usersMessages.containsKey(chatId)) {
                    usersMessages.put(chatId, usersMessages.get(chatId) + "\n" + message);
                } else {
                    usersMessages.put(chatId, "Less than 24 hours left till:\n" + message);
                }
                usersEvent.setNotified(true);
            }
        }));
        sendMessagesToAll(usersMessages);
    }

    @Scheduled(cron = "0 0 8,12,16,20 * * *")
    @Transactional
    public void parseAllUsersTasks() {
        log.info("parseAllUsersTasks - checking if there are any groups or courses that need their events updated.");
        List<User> users = getUsers();
        users.forEach(user -> {
            if (shouldParse(user))
                eventsFunction.apply(user);
        });
    }

    private void sendMessagesToAll(Map<Long, String> usersMessages) {
        usersMessages.forEach((chatId, message) -> {
            SendMessage sendMessage = TelegramUtil.createSendMessage(chatId, message);
            sendMessageConsumer.accept(sendMessage);
        });
    }

    @Transactional(readOnly = true)
    public boolean shouldParse(User user) {
        user = userRepository.getById(user.getId());
        List<Group> groups = user.getGroups();
        for (Group group :
                groups) {
            if (group.getUpdatedAt() == null || group.getUpdatedAt().isBefore(Instant.now().minus(Duration.ofHours(3)))) {
                log.info("Parsing user's events {}", user.getChatId());
                return true;
            }
        }
        List<Course> courses = user.getCourses();
        for (Course course :
                courses
        ) {
            if (course.getUpdatedAt() == null || course.getUpdatedAt().isBefore(Instant.now().minus(Duration.ofHours(3)))) {
                log.info("Parsing user's events {}", user.getChatId());
                return true;
            }
        }
        return false;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public boolean loadUser(LoginRequest loginRequest, String chatId) {

        log.info("loadUser - starting to load user's data.");

        try {
            if (userRepository.findByChatId(Long.parseLong(chatId)).isPresent()) {
            User registered = userRepository.getByChatId(Long.parseLong(chatId));
                UserTokenDTO dto = webService.getToken(loginRequest.username(), loginRequest.password());
            registered.setToken(dto.token());
            registered.setUserId(webService.getUserId(registered.getToken()));
            userRepository.save(registered);
            registered.setState(State.LOGGED_IN);
            userRepository.save(registered);
            return true;
        } else {
            log.info("loadUser - Couldn't find user {}", chatId);
            return false;
        }
        }catch (ResponseStatusException e){
            userRepository.getByChatId(Long.parseLong(chatId)).setState(State.START);
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "User credentials are wrong or moodle is down.");
        }
    }

    @Async
    @Transactional
    public void loadUsersFields(String chatId) {
        User user = userRepository.findByChatId(Long.parseLong(chatId)).orElseThrow();

        log.info("loadUsersFields - Loading user groups and courses.");

        loadUserGroups(user);
        loadUserCourses(user);
        user = userRepository.save(user);
        if (shouldParse(user))
            eventsFunction.apply(user);

        sendMessageConsumer.accept(TelegramUtil.createSendMessage(chatId, """
                Successfully found u in my database <3
                You're automatically subscribed to the bot's notifications regarding upcoming events.
                Send /upcoming to get all the currently saved events."""));
        log.info("loadUsersFields - Successfully loaded user's fields");
    }

    @Transactional
    public void loadUserCourses(User user) {
        List<CourseDTO> userCourses = webService.getCourses(user.getToken(), user.getUserId());
        for (CourseDTO courseDTO :
                userCourses) {
            Course course = courseRepository.findById(courseDTO.id())
                    .orElseGet(()->courseRepository.save(Course.fromDTO(courseDTO)));
            course.addUser(user);
            courseRepository.save(course);
        }
    }

    @Transactional
    public void loadUserGroups(User user) {
        List<GroupDTO> userGroups = webService.getGroups(user.getToken());
        for (GroupDTO groupDTO : userGroups) {
            Group group = groupRepository.findById(groupDTO.id())
                    .orElseGet(()->groupRepository.save(new Group(groupDTO.id(), groupDTO.name())));
            group.addUser(user);
            groupRepository.save(group);
        }
    }

    public User getUserById(String chatId) {
        return userRepository.findByChatId(Long.parseLong(chatId)).orElseThrow(RuntimeException::new);
    }

    public List<Course> getCourses(User user) {
        return courseRepository.getCoursesByUsersContaining(user);
    }
}
