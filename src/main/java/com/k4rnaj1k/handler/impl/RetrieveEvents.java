package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.UserHandler;
import com.k4rnaj1k.model.Course;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import com.k4rnaj1k.service.UserService;
import com.k4rnaj1k.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.k4rnaj1k.util.TelegramUtil.formatEvents;

@Component
@Slf4j
public class RetrieveEvents implements UserHandler {

    private final EventService eventService;
    private final UserService userService;
    @Value("${admin.chatid}")
    private Long adminChatId;

    public RetrieveEvents(EventService eventService, @Lazy UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update) {
        return retrieveEvents(user, update.getMessage().getText());
    }

    private List<PartialBotApiMethod<? extends Serializable>> retrieveEvents(User user, String message) {
        List<PartialBotApiMethod<? extends Serializable>> result = new ArrayList<>();
        if (Objects.equals(message, "/upcoming")) {
            log.info("User issued upcoming command.");
            return getUpcoming(user);
        } else if (Objects.equals(message, "/courses")) {
            log.info("User issued courses command.");
            SendMessage coursesList = TelegramUtil.createSendMessage(user.getChatId(), getCourseList(user));
            return List.of(coursesList);
        } else if (Objects.equals(message, "/update") && Objects.equals(user.getChatId(), adminChatId)) {
            userService.parseAllUsersTasks();
            return List.of(TelegramUtil.createSendMessage(user.getChatId(), "Successfully parsed all the users events."));
        } else if (Objects.equals(message, "/update-all") && Objects.equals(user.getChatId(), adminChatId)) {
            userService.loadAllUsersFields();
            return List.of(TelegramUtil.createSendMessage(user.getChatId(), "Successfully parsed all user data."));
        }
        return result;
    }

    private List<PartialBotApiMethod<? extends Serializable>> getUpcoming(User user) {
        List<PartialBotApiMethod<? extends Serializable>> result = new ArrayList<>();
        List<Event> tomorrowEvents = eventService.getTomorrow(user);
        if (tomorrowEvents.size() != 0) {
            SendMessage tomorrowMessage = TelegramUtil.createSendMessage(user.getChatId(), "Tomorrow" + "\n" +
                    formatEvents(tomorrowEvents, false));
            result.add(tomorrowMessage);
        }
        List<Event> thisWeekEvents = eventService.getThisWeek(user);
        if (thisWeekEvents.size() != 0) {
            SendMessage thisWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), "This week" + "\n" + formatEvents(thisWeekEvents, true));
            result.add(thisWeekMessage);
        }

        List<Event> afterWeekEvents = eventService.getAfterWeek(user);
        if (afterWeekEvents.size() != 0) {
            SendMessage afterWeekMessage = TelegramUtil.createSendMessage(user.getChatId(), "After this week" + "\n" + formatEvents(afterWeekEvents, true));
            result.add(afterWeekMessage);
        }
        if (result.isEmpty()) {
            result.add(TelegramUtil.createSendMessage(user.getChatId(), "Nothing to show."));
        }
        return result;
    }

    private String getCourseList(User user) {
        List<Course> userCourses = userService.getCourses(user);
        String res = "Your courses: \n";
        for (Course course :
                userCourses) {
            res = res.concat(course.getFullName() + " - " + course.getShortName() + "\n");
        }
        return res;
    }

    @Override
    public State operatedBotState() {
        return State.LOGGED_IN;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return Collections.emptyList();
    }
}
