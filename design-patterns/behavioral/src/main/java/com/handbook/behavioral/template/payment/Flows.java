package com.handbook.behavioral.template.payment;

public final class Flows {

    private Flows() {
    }

    public static class CardFlow extends PaymentFlow {
        protected String method() { return "card"; }

        @Override
        protected boolean requiresStrongAuth(int amount) { return amount > 5_000; } // step-up authentication for large amounts

        @Override
        protected void stepUp(String orderId) { System.out.println("  [card] 3-D Secure OTP verified"); }

        protected String authorize(String orderId, int amount) {
            System.out.println("  [card] authorization hold INR " + amount);
            return "AUTH-" + orderId;
        }

        protected String capture(String auth, int amount) {
            System.out.println("  [card] captured hold " + auth);
            return "CAP-" + auth;
        }
    }

    public static class UpiFlow extends PaymentFlow {
        protected String method() { return "upi"; }

        protected String authorize(String orderId, int amount) {
            System.out.println("  [upi] collect request approved in UPI app");
            return "UPI-" + orderId;
        }

        protected String capture(String auth, int amount) {
            return auth + "-SETTLED"; // UPI debits immediately; nothing extra to capture
        }
    }
}
