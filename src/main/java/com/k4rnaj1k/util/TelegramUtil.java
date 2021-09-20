package com.k4rnaj1k.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TelegramUtil {
    public static SendMessage createSendMessage(Long chatId, String message){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.enableMarkdownV2(true);
        return sendMessage;
    }
}
