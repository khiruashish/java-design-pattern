package com.handbook.creational.factorymethod.notification;

public record PushNotification() implements Notification {

    @Override
    public String send(String recipient, String message) {
        return "[PUSH]  device=%s title=\"Order update\" text=\"%s\"".formatted(recipient, message);
    }
}
