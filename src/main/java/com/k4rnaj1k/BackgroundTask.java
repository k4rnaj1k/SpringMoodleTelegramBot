package com.k4rnaj1k;

import com.k4rnaj1k.bot.Bot;
import com.k4rnaj1k.dto.UpcomingEventDTO;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.Group;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.service.EventService;
import com.k4rnaj1k.service.UserService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Component
public class BackgroundTask extends TimerTask {
    private final UserService userService;
    private final EventService eventService;
    private final Bot bot;

    public BackgroundTask(UserService userService, EventService eventService, Bot bot) {
        this.userService = userService;
        this.eventService = eventService;
        this.bot = bot;
        Timer timer = new Timer();
        timer.schedule(this, TimeUnit.MINUTES.toMillis(1), TimeUnit.HOURS.toMillis(5));
    }

    public void run() {
        List<User> users = userService.getUsers();
        users.forEach(user ->
        {
            if (shouldParse(user.getGroups())) {
                List<Event> events = eventService.getEvents(user.getToken());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                events.forEach(event ->
                        {
                            if (event.timeStart().isBefore(Instant.now().plus(7, ChronoUnit.DAYS))) {

                                SendMessage sendMessage = TelegramUtil.createSendMessage(user.getChatId(), simpleDateFormat.format(Date.from(event.timeStart())) + " " + event.name() + " " + event.moduleName());
                                bot.executeWithExceptionCheck(sendMessage);
                            }
                        }
                );
            }

        });
    }

    private boolean shouldParse(List<Group> groups) {
        return groups.stream().anyMatch(group -> group.getUpdatedAt().isBefore(Instant.now().minus(Duration.ofHours(4))));
    }
}