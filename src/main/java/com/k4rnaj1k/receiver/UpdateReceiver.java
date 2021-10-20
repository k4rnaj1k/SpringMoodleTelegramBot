package com.k4rnaj1k.receiver;

import com.k4rnaj1k.handler.GroupHandler;
import com.k4rnaj1k.handler.UserHandler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UserChat;
import com.k4rnaj1k.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class UpdateReceiver {
    private final List<UserHandler> userHandlers;
    private final List<GroupHandler> groupHandlers;
    private final UserService userService;

    public UpdateReceiver(List<UserHandler> userHandlers, List<GroupHandler> groupHandlers, @Lazy UserService userService) {
        this.userHandlers = userHandlers;
        this.groupHandlers = groupHandlers;
        this.userService = userService;
    }

    @Transactional
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if (isMessageWithText(update)) {
                Long chatId = update.getMessage().getChatId();
                User user = userService.getUserById(chatId);
                return getHandleByState(user.getState()).handle(user, update);
            } else if (isChatStatusUpdate(update)) {
                Long chatId = update.getMyChatMember().getFrom().getId();
                Long groupChatId = update.getMyChatMember().getChat().getId();
                if (update.getMyChatMember().getNewChatMember().getStatus().equals("left")) {
                    userService.removeGroupChatById(groupChatId);
                    return Collections.emptyList();
                } else if (update.getMyChatMember().getNewChatMember().getStatus().equals("member")) {
                    UserChat userChat = userService.getUserChatById(groupChatId, chatId);
                    List<PartialBotApiMethod<? extends Serializable>> result = getGroupHandlerByState(userChat.getState()).handle(userChat, update);
                    userChat.setState(State.CHAT_LOGGED_IN);
                    return result;
                }else{
                    throw new UnsupportedOperationException();
                }
            } else if (isFromChatUser(update)) {
                User user = userService.getUserById(update.getMessage().getFrom().getId());
                UserChat userChat = userService.getUserChatById(update.getMessage().getChat().getId(), user.getChatId());
                return getGroupHandlerByState(userChat.getState()).handle(userChat, update);
            } else
                throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            return Collections.emptyList();
        }
    }

    private UserHandler getHandleByState(State state) {
        return userHandlers.stream().filter(userHandler -> userHandler.operatedBotState() != null)
                .filter(userHandler -> userHandler.operatedBotState() == state)
                .findFirst().orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isFromChatUser(Update update) {
        return update.getMessage().getChat().getType().equals("group");
    }

    private boolean isChatStatusUpdate(Update update) {
//        return update.getMyChatMember() != null || update.getMessage().getChat().getType().equals("group");
        return update.getMyChatMember() != null;
    }

    private GroupHandler getGroupHandlerByState(State state) {
        return groupHandlers.stream().filter(groupHandler -> groupHandler.operatedBotState() != null)
                .filter(groupHandler -> groupHandler.operatedBotState() == state)
                .findFirst().orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return (!update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getChat().getType().equals("group"));
    }
}
