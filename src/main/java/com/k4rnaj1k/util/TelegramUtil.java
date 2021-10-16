package com.k4rnaj1k.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

    public static SendMessage createSendMessage(String chatId, String message) {
        return createSendMessage(Long.parseLong(chatId), message);
    }

    public static SendMessage createSendMessageWithUrl(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.enableMarkdownV2(true);
        return sendMessage;
    }
}
