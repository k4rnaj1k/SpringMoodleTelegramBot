package com.k4rnaj1k.bot;

import com.k4rnaj1k.receiver.UpdateReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {
    Logger log = LoggerFactory.getLogger(Bot.class);
    @Value("${bot.token}")
    private String token;
    @Value("${bot.username}")
    private String botUsername;

    private UpdateReceiver updateReceiver;

    public Bot(UpdateReceiver updateReceiver) {
        this.updateReceiver = updateReceiver;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.hasCallbackQuery());
        List<PartialBotApiMethod<? extends Serializable>> messages = updateReceiver.handle(update);
        if (messages != null && !messages.isEmpty()) {
            messages.forEach(message ->
            {
                if (message instanceof SendMessage)
                    executeWithExceptionCheck((SendMessage) message);
            });
        }
    }

    private void executeWithExceptionCheck(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
