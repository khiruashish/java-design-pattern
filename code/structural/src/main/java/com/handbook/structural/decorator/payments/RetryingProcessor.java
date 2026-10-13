package com.handbook.structural.decorator.payments;

public class RetryingProcessor extends PaymentDecorator {

    private final int maxAttempts;

    public RetryingProcessor(PaymentProcessor inner, int maxAttempts) {
        super(inner);
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String pay(String orderId, int amountInr) {
        for (int attempt = 1; ; attempt++) {
            try {
                return super.pay(orderId, amountInr);
            } catch (IllegalStateException e) {
                if (attempt == maxAttempts) {
                    throw e;
                }
                System.out.println("  [retry] attempt " + attempt + " failed (" + e.getMessage() + "), retrying");
            }
        }
    }
}
