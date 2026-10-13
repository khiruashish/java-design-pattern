package com.handbook.creational.factorymethod.payments;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry-based factory: new payment methods are added by registering a
 * supplier, not by editing a switch statement (Open/Closed Principle).
 */
public final class PaymentProcessorFactory {

    private final Map<PaymentMethod, Supplier<? extends PaymentProcessor>> registry =
            new EnumMap<>(PaymentMethod.class); // (1)!

    public PaymentProcessorFactory register(PaymentMethod method,
                                            Supplier<? extends PaymentProcessor> supplier) {
        registry.put(method, supplier);
        return this; // fluent registration
    }

    public PaymentProcessor create(PaymentMethod method) {
        Supplier<? extends PaymentProcessor> supplier = registry.get(method);
        if (supplier == null) {
            throw new UnsupportedPaymentMethodException(method); // (2)!
        }
        return supplier.get(); // (3)!
    }
}
