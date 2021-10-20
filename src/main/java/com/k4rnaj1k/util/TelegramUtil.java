package com.k4rnaj1k.util;

import com.k4rnaj1k.model.Event;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public class TelegramUtil {
    public static SendMessage createSendMessage(Long chatId, String message) {
        message = message.replaceAll("\\.", "\\\\.")
                .replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)")
                .replaceAll("-", "\\\\-").replaceAll("is due", "")
                .replaceAll("!", "\\\\!");
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.enableMarkdownV2(true);
        return sendMessage;
    }

    public static String formatEvents(List<Event> events, boolean afterTomorrow) {
        String res = "";
        DateTimeFormatter tomorrowFormat = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
        DateTimeFormatter weekFormat = new DateTimeFormatterBuilder().appendPattern("dd.MM").toFormatter();
        for (Event event :
                events) {
            res = res.concat((event.getCourse() != null ? event.getCourse().getShortName() : event.getGroup().getName()) + " " + event.getName() + " ");
            if (!afterTomorrow)
                res = res.concat(tomorrowFormat.format(LocalDateTime.ofInstant(event.getTimeStart(), ZoneId.of("Europe/Kiev"))));
            else {
                res = res.concat(weekFormat.format(LocalDateTime.ofInstant(event.getTimeStart(), ZoneId.of("Europe/Kiev"))));
            }
            res = res.concat("\n");
        }
        return res;
    }

    public static SendMessage createSendMessage(String chatId, String message) {
        return createSendMessage(Long.parseLong(chatId), message);
    }

    public static SendMessage createSendMessageWithUrl(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.enableMarkdownV2(true);
        return sendMessage;
    }

    public static String acronym(String phrase) {
        StringBuilder result = new StringBuilder();
        for (String token : phrase.split("\\s+")) {
            result.append(token.toUpperCase().charAt(0));
        }
        return result.toString();
    }
}
