package com.handbook.capstone.payment;

import com.handbook.capstone.shared.Money;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/** Payment module: a port, an adapter for an external provider, a factory, and a circuit breaker. */
public final class Payments {

    private Payments() {
    }

    public record Charge(String reference, String customerId, Money amount) {
    }

    public sealed interface Result permits Paid, Declined, Unavailable {
    }

    public record Paid(String transactionId) implements Result {
    }

    public record Declined(String reason) implements Result {
    }

    public record Unavailable(String reason) implements Result {
    }

    /** The port the checkout depends on. */
    public interface PaymentGateway {
        Result charge(Charge charge);

        void refund(String reference);
    }

    /** A third-party SDK with its own vocabulary (we can't change it). */
    public interface PayFastSdk {
        Map<String, String> createPayment(String merchantKey, String amountRupees, String orderRef);

        void reverse(String orderRef);
    }

    /** [Adapter] Translates our port into PayFast's API and its status codes back into our Result. */
    public static final class PayFastAdapter implements PaymentGateway {
        private final PayFastSdk sdk;

        public PayFastAdapter(PayFastSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public Result charge(Charge c) {
            Map<String, String> r = sdk.createPayment("MK-FRESHCART",
                    String.format("%d.%02d", c.amount().paise() / 100, c.amount().paise() % 100), c.reference());
            return switch (r.get("status")) {
                case "CAPTURED" -> new Paid(r.get("txn"));
                case "DECLINED" -> new Declined(r.get("reason"));
                default -> new Unavailable("unexpected PayFast status " + r.get("status"));
            };
        }

        @Override
        public void refund(String reference) {
            sdk.reverse(reference);
        }
    }

    /** Cash on delivery: always "succeeds" now; money is collected at the door. */
    public static final class CashOnDelivery implements PaymentGateway {
        @Override
        public Result charge(Charge c) {
            return new Paid("COD-" + c.reference());
        }

        @Override
        public void refund(String reference) {
        }
    }

    /**
     * [Decorator] + [Circuit Breaker]: wraps any gateway. After N consecutive failures it stops
     * calling the provider for a cool-down period and fails fast.
     */
    public static final class CircuitBreakerGateway implements PaymentGateway {
        private final PaymentGateway inner;
        private final int threshold;
        private final Duration openFor;
        private final Clock clock;
        private int consecutiveFailures;
        private Instant openedAt;

        public CircuitBreakerGateway(PaymentGateway inner, int threshold, Duration openFor, Clock clock) {
            this.inner = inner;
            this.threshold = threshold;
            this.openFor = openFor;
            this.clock = clock;
        }

        @Override
        public synchronized Result charge(Charge c) {
            if (openedAt != null && clock.instant().isBefore(openedAt.plus(openFor))) {
                return new Unavailable("payment circuit OPEN, failing fast");
            }
            Result r;
            try {
                r = inner.charge(c);
            } catch (RuntimeException e) {
                r = new Unavailable(e.getMessage());
            }
            if (r instanceof Unavailable) {
                if (++consecutiveFailures >= threshold) {
                    openedAt = clock.instant();
                }
            } else {
                consecutiveFailures = 0;
                openedAt = null;
            }
            return r;
        }

        @Override
        public void refund(String reference) {
            inner.refund(reference);
        }

        public synchronized boolean isOpen() {
            return openedAt != null && clock.instant().isBefore(openedAt.plus(openFor));
        }
    }

    /** [Factory Method] The checkout asks for "a gateway for UPI"; the factory decides which class. */
    public static final class GatewayFactory {
        private final Map<String, PaymentGateway> byMethod;

        public GatewayFactory(Map<String, PaymentGateway> byMethod) {
            this.byMethod = byMethod;
        }

        public PaymentGateway forMethod(String method) {
            PaymentGateway g = byMethod.get(method);
            if (g == null) {
                throw new IllegalArgumentException("unsupported payment method " + method);
            }
            return g;
        }
    }
}
