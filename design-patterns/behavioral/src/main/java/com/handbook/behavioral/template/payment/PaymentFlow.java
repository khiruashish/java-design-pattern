package com.handbook.behavioral.template.payment;

/** Template for every payment method: the order of steps is a compliance requirement. */
public abstract class PaymentFlow {

    public final String process(String orderId, int amountInr) {
        validate(orderId, amountInr);
        if (requiresStrongAuth(amountInr)) {        // hook
            stepUp(orderId);
        }
        String auth = authorize(orderId, amountInr); // abstract
        String capture = capture(auth, amountInr);   // abstract
        notifyCustomer(orderId, capture);            // fixed
        return capture;
    }

    private void validate(String orderId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        System.out.println("  [" + method() + "] validated " + orderId + " INR " + amount);
    }

    protected boolean requiresStrongAuth(int amountInr) { return false; }

    protected void stepUp(String orderId) { }

    protected abstract String method();

    protected abstract String authorize(String orderId, int amountInr);

    protected abstract String capture(String authorization, int amountInr);

    private void notifyCustomer(String orderId, String reference) {
        System.out.println("  [" + method() + "] receipt for " + orderId + ": " + reference);
    }
}
