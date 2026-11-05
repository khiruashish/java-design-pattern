package com.handbook.creational.factorymethod.notification;

/** Static factory method: the only place that knows the concrete classes. */
public final class NotificationFactory {

    private NotificationFactory() {
    }

    public static Notification create(Channel channel) {
        return switch (channel) { // (1)!
            case EMAIL -> new EmailNotification();
            case SMS -> new SmsNotification();
            case PUSH -> new PushNotification();
        };
    }
}
