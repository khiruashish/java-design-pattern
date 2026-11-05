package com.handbook.creational.factorymethod.notification;

public record SmsNotification() implements Notification {

    private static final int MAX_LENGTH = 40; // short for the demo; real SMS allows 160

    @Override
    public String send(String recipient, String message) {
        String text = message.length() > MAX_LENGTH
                ? message.substring(0, MAX_LENGTH - 3) + "..."
                : message;
        return "[SMS]   to=%s text=\"%s\"".formatted(recipient, text);
    }
}
