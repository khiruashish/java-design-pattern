package com.handbook.creational.factorymethod.payments;

public interface PaymentProcessor {

    PaymentResult pay(String orderId, int amountInr);
}
