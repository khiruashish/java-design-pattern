package com.handbook.enterprise.di.grocery;

import java.util.ArrayList;
import java.util.List;

/** Ports the checkout needs. The service never decides WHICH implementation it gets. */
public final class Checkout {

    public interface PaymentGateway { String charge(String orderId, long paise); }

    public interface SlotBooker { String book(String pincode); }

    public interface Notifier { void send(String to, String message); }

    /** High-level policy: depends only on the three interfaces, all supplied through the constructor. */
    public static final class CheckoutService {
        private final PaymentGateway payments;
        private final SlotBooker slots;
        private final Notifier notifier;

        public CheckoutService(PaymentGateway payments, SlotBooker slots, Notifier notifier) { // (1)!
            this.payments = payments;
            this.slots = slots;
            this.notifier = notifier;
        }

        public String placeOrder(String orderId, String customer, String pincode, long paise) {
            String paymentRef = payments.charge(orderId, paise);
            String slot = slots.book(pincode);
            notifier.send(customer, "Order " + orderId + " confirmed for " + slot);
            return orderId + " paid (" + paymentRef + "), slot " + slot;
        }
    }

    // ---- Implementations: production-like and demo/test-friendly ----

    public static final class RazorPayGateway implements PaymentGateway {
        public String charge(String orderId, long paise) { return "rzp_" + orderId + "_" + paise; }
    }

    public static final class FakeGateway implements PaymentGateway {
        public String charge(String orderId, long paise) { return "fake-ok"; }
    }

    public static final class DarkStoreSlots implements SlotBooker {
        public String book(String pincode) { return pincode.startsWith("560") ? "Today 18:00-19:00" : "Tomorrow 09:00-10:00"; }
    }

    public static final class SmsNotifier implements Notifier {
        public void send(String to, String message) { System.out.println("  [SMS to " + to + "] " + message); }
    }

    public static final class RecordingNotifier implements Notifier {
        public final List<String> sent = new ArrayList<>();
        public void send(String to, String message) { sent.add(to + ": " + message); }
    }

    private Checkout() {
    }
}
