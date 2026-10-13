package com.handbook.behavioral.observer.bus;

public class EventBusDemo {

    record UserSignedUp(String email) {
    }

    record PasswordChanged(String email) {
    }

    public static void main(String[] args) throws Exception {
        EventBus bus = new EventBus();
        AutoCloseable welcome = bus.subscribe(UserSignedUp.class, e -> System.out.println("welcome mail -> " + e.email()));
        bus.subscribe(UserSignedUp.class, e -> System.out.println("CRM contact  -> " + e.email()));
        bus.subscribe(PasswordChanged.class, e -> System.out.println("security mail -> " + e.email()));

        bus.publish(new UserSignedUp("neha@example.com"));
        bus.publish(new PasswordChanged("neha@example.com"));
        welcome.close();
        bus.publish(new UserSignedUp("li@example.com"));
    }
}
