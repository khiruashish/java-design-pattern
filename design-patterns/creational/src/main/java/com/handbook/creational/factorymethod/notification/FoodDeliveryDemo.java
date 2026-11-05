package com.handbook.creational.factorymethod.notification;

import com.handbook.creational.factorymethod.notification.OrderStatusNotifier.Customer;

import java.util.List;

public class FoodDeliveryDemo {

    public static void main(String[] args) {
        System.out.println("=== Food Delivery: Order Status Notifications ===");
        OrderStatusNotifier notifier = new OrderStatusNotifier();

        List<Customer> customers = List.of(
                new Customer("Asha", Channel.EMAIL, "asha@example.com"),
                new Customer("Ravi", Channel.SMS, "+91-98450-00001"),
                new Customer("Meera", Channel.PUSH, "device-7f3a"));

        int orderNo = 501;
        for (Customer c : customers) {
            System.out.println(notifier.notify(c, "FD-" + orderNo++, "out for delivery"));
        }
    }
}
