package com.handbook.architecture.hexagonal.checkout.adapter.out;

import com.handbook.architecture.hexagonal.checkout.port.out.NotificationPort;

/** Driven adapter: sends an SMS (printed here instead of calling an SMS gateway). */
public class SmsNotificationAdapter implements NotificationPort {

    @Override
    public void notify(String phone, String message) {
        System.out.println("    sms to " + phone + ": " + message);
    }
}
