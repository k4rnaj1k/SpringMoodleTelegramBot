package com.k4rnaj1k.receiver;

import com.k4rnaj1k.handler.GroupHandler;
import com.k4rnaj1k.handler.UserHandler;
import com.k4rnaj1k.model.State;
import com.k4rnaj1k.model.User;
import com.k4rnaj1k.model.UserChat;
import com.k4rnaj1k.repository.UserChatRepository;
import com.k4rnaj1k.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Component
public class UpdateReceiver {
    private final List<UserHandler> userHandlers;
    private final List<GroupHandler> groupHandlers;
    private final UserRepository userRepository;
    private final UserChatRepository userChatRepository;

    public UpdateReceiver(List<UserHandler> userHandlers, List<GroupHandler> groupHandlers, UserRepository userRepository, UserChatRepository userChatRepository) {
        this.userHandlers = userHandlers;
        this.groupHandlers = groupHandlers;
        this.userRepository = userRepository;
        this.userChatRepository = userChatRepository;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {
        try {
            if (isMessageWithText(update)) {
                Long chatId = update.getMessage().getChatId();
                User user = userRepository.findByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                return getHandleByState(user.getState()).handle(user, update);
//            } else if (update.hasCallbackQuery()) {
//                CallbackQuery callbackQuery = update.getCallbackQuery();
//                return getHandlerByQuery(callbackQuery.getData()).handle(update, message.getText());
            } else if (isFromChat(update)) {
                Long chatId;
                Long groupChatId;
                if (update.getMessage() != null) {
                    chatId = update.getMessage().getFrom().getId();
                    groupChatId = update.getMessage().getChatId();
                } else {
                    groupChatId = update.getMyChatMember().getChat().getId();
                    chatId = update.getMyChatMember().getFrom().getId();
                }
                User user;
                if (userRepository.existsByChatId(chatId)) {
                    user = userRepository.getByChatId(chatId);
                } else {
                    user = userRepository.save(new User(chatId));
                }
                UserChat chat = userChatRepository.findByChatId(groupChatId).orElseGet(() -> userChatRepository.save(new UserChat(user, groupChatId)));
                return getGroupHandlerByState(chat.getState()).handle(chat, update);
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

    private boolean isFromChat(Update update) {
        return update.getMyChatMember() != null || update.getMessage().getChat().getType().equals("group");
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
