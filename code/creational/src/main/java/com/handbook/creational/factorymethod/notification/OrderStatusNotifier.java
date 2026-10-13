package com.handbook.creational.factorymethod.notification;

public class OrderStatusNotifier {

    public record Customer(String name, Channel preferredChannel, String address) {
    }

    public String notify(Customer customer, String orderId, String status) {
        Notification notification = NotificationFactory.create(customer.preferredChannel()); // (1)!
        String message = "Hi %s, order %s is %s".formatted(customer.name(), orderId, status);
        return notification.send(customer.address(), message);
    }
}
