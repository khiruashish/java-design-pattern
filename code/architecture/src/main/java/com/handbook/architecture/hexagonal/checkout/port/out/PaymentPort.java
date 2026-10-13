package com.handbook.architecture.hexagonal.checkout.port.out;

/** Driven (secondary) port: what the core needs from a payment provider, in the core's terms. */
public interface PaymentPort {
    boolean charge(String customerId, long amountPaise, String reference);
}
