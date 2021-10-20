package com.k4rnaj1k.handler.impl;

import com.k4rnaj1k.handler.GroupHandler;
import com.k4rnaj1k.model.Event;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UserChat;
import com.k4rnaj1k.service.EventService;
import com.k4rnaj1k.service.UserService;
import com.k4rnaj1k.util.TelegramUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.k4rnaj1k.util.TelegramUtil.formatEvents;

@Component
public class GroupUserMessageHandler implements GroupHandler {
    private final UserService userService;
    private final EventService eventService;

    public GroupUserMessageHandler(@Lazy UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(UserChat userChat, Update update) {
        List<PartialBotApiMethod<? extends Serializable>> result = new ArrayList<>();
        if (update.getMessage().getText().replaceAll("@" + System.getenv("BOT_USR"), "").equals("/upcoming")) {
            User user;
            if(!userService.getUserById(update.getMessage().getFrom().getId()).getState().equals(State.LOGGED_IN))
                user = userChat.getUser();
            else
                user = userService.getUserById(update.getMessage().getFrom().getId());
            List<Event> tomorrowEvents = eventService.getTomorrow(user);
            if (tomorrowEvents.size() != 0) {
                SendMessage tomorrowMessage = TelegramUtil.createSendMessage(userChat.getChatId(), "Tomorrow" + "\n" +
                        formatEvents(tomorrowEvents, false));
                result.add(tomorrowMessage);
            }
            List<Event> thisWeekEvents = eventService.getThisWeek(user);
            if (thisWeekEvents.size() != 0) {
                SendMessage thisWeekMessage = TelegramUtil.createSendMessage(userChat.getChatId(), "This week" + "\n" + formatEvents(thisWeekEvents, true));
                result.add(thisWeekMessage);
            }

            List<Event> afterWeekEvents = eventService.getAfterWeek(user);
            if (afterWeekEvents.size() != 0) {
                SendMessage afterWeekMessage = TelegramUtil.createSendMessage(userChat.getChatId(), "After this week" + "\n" + formatEvents(afterWeekEvents, true));
                result.add(afterWeekMessage);
            }
            return result;
        }
        return List.of();
    }

    @Override
    public State operatedBotState() {
        return State.CHAT_LOGGED_IN;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return null;
    }
}
