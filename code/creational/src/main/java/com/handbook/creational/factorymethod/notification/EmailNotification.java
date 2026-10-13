package com.handbook.creational.factorymethod.notification;

public record EmailNotification() implements Notification {

    @Override
    public String send(String recipient, String message) {
        return "[EMAIL] to=%s subject=\"Order update\" body=\"%s\"".formatted(recipient, message);
    }
}
