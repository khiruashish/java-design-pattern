package com.handbook.architecture.hexagonal.checkout.port.out;

/** Driven port for telling the customer something. SMS, push or e-mail is an adapter decision. */
public interface NotificationPort {
    void notify(String phone, String message);
}
