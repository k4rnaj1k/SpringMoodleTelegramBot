package com.k4rnaj1k.exception;

public class BotExceptionUtils {

    public static RuntimeException courseParseFailed(Long userId) {
        return new RuntimeException("Couldn't parse user's courses (userid = " + userId + ").");
    }

    public static RuntimeException didntRetrieveUserId() {
        return new RuntimeException("Couldn't retrieve userid. Mentor could be down...");
    }
}
