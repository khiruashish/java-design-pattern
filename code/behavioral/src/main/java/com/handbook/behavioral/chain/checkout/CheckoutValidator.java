package com.handbook.behavioral.chain.checkout;

import java.util.Optional;

/** Handler: validates one thing, then passes the order to the next validator. */
public abstract class CheckoutValidator {

    private CheckoutValidator next;

    public CheckoutValidator then(CheckoutValidator next) { // (1)!
        this.next = next;
        return next;
    }

    public final Optional<String> validate(Order order) {
        Optional<String> problem = check(order);
        if (problem.isPresent()) {
            return problem; // (2)!
        }
        return next == null ? Optional.empty() : next.validate(order); // (3)!
    }

    protected abstract Optional<String> check(Order order);
}
