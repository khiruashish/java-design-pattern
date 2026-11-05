package com.handbook.creational.factorymethod.notification;

/** The product. Callers only ever see this interface. */
public sealed interface Notification
        permits EmailNotification, SmsNotification, PushNotification { // (1)!

    String send(String recipient, String message);
}
