package com.handbook.behavioral.command.banking;

/** Commands as plain data: easy to persist, queue, audit and replay. */
public sealed interface PaymentCommand {

    String id();

    record Transfer(String id, String from, String to, long amountInr) implements PaymentCommand {
    }

    record PayBill(String id, String from, String biller, long amountInr) implements PaymentCommand {
    }

    record MobileRecharge(String id, String from, String phone, long amountInr) implements PaymentCommand {
    }
}
