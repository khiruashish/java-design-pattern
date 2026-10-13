package com.handbook.lld.notification;

import com.handbook.lld.notification.NotificationService.Channel;
import com.handbook.lld.notification.NotificationService.MessageTemplate;
import com.handbook.lld.notification.NotificationService.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationDemo {

    public static void main(String[] args) {
        List<String> retryLog = new ArrayList<>();
        int[] smsGatewayBlips = {1};
        var service = new NotificationService()
                .register(Channel.EMAIL, (u, text) -> System.out.println("  EMAIL to " + u.email() + ": " + text))
                .register(Channel.SMS, NotificationService.withRetry((u, text) -> {
                    if (smsGatewayBlips[0]-- > 0) {
                        throw new IllegalStateException("SMS gateway 503");
                    }
                    System.out.println("  SMS   to " + u.phone() + ": " + text);
                }, 3, retryLog))
                .register(Channel.PUSH, (u, text) -> {
                    throw new IllegalStateException("device token expired");
                });

        MessageTemplate outForDelivery = new MessageTemplate() {
            @Override
            protected String body(Map<String, String> d) {
                return "order " + d.get("order") + " is out for delivery with " + d.get("rider") + ", arriving by " + d.get("eta") + ".";
            }
        };

        System.out.println("=== LLD: Notification system ===");
        User asha = new User("U1", "Asha", "asha@example.com", "+91-98450-11111", Set.of(Channel.EMAIL, Channel.SMS, Channel.PUSH));
        User ravi = new User("U2", "Ravi", "ravi@example.com", "+91-98450-22222", Set.of(Channel.EMAIL));
        Map<String, String> data = Map.of("order", "ORD-9", "rider", "Imran", "eta", "18:30");
        service.notify(asha, outForDelivery, data);
        service.notify(ravi, outForDelivery, data);
        System.out.println("retry log: " + retryLog);
        System.out.println("failures : " + service.failures());
    }
}
