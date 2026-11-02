import java.util.*;

/** Problem: checkout expects PaymentGateway; integrate a legacy SDK and a third-party API with incompatible interfaces. */
public class AdapterPayments {

    /** Target interface our code depends on (amounts in minor units, our own result type). */
    interface PaymentGateway { PaymentResult charge(String orderId, long amountCents, String currency); }
    record PaymentResult(boolean success, String reference, String error) { }

    /** Adaptee 1: legacy SDK with rupees as double, status codes, and checked exceptions. */
    static final class LegacyBankSdk {
        int pay(String ref, double rupees) throws Exception {
            if (rupees > 10_000) throw new Exception("LIMIT_EXCEEDED");
            return rupees > 0 ? 0 : 51;                         // 0 = OK, 51 = invalid amount
        }
    }

    /** Adaptee 2: modern third-party client with a map-based request and string statuses. */
    static final class StripeLikeClient {
        Map<String, String> createCharge(Map<String, Object> req) {
            return Map.of("id", "ch_" + req.get("metadata_order"), "status", ((Long) req.get("amount")) % 2 == 0 ? "succeeded" : "failed");
        }
    }

    /** Object adapter: wraps the adaptee and translates units, errors and status codes into our model. */
    static final class LegacyBankAdapter implements PaymentGateway {
        private final LegacyBankSdk sdk;
        LegacyBankAdapter(LegacyBankSdk sdk) { this.sdk = sdk; }

        public PaymentResult charge(String orderId, long amountCents, String currency) {
            if (!currency.equals("INR")) return new PaymentResult(false, null, "UNSUPPORTED_CURRENCY");
            try {
                int code = sdk.pay(orderId, amountCents / 100.0);
                return code == 0 ? new PaymentResult(true, "BANK-" + orderId, null) : new PaymentResult(false, null, "CODE_" + code);
            } catch (Exception e) {
                return new PaymentResult(false, null, e.getMessage());   // checked exception -> domain result
            }
        }
    }

    static final class StripeAdapter implements PaymentGateway {
        private final StripeLikeClient client;
        StripeAdapter(StripeLikeClient client) { this.client = client; }

        public PaymentResult charge(String orderId, long amountCents, String currency) {
            Map<String, String> resp = client.createCharge(Map.of("amount", amountCents, "currency", currency.toLowerCase(Locale.ROOT), "metadata_order", orderId));
            boolean ok = "succeeded".equals(resp.get("status"));
            return new PaymentResult(ok, ok ? resp.get("id") : null, ok ? null : "DECLINED");
        }
    }

    /** Client code: unaware of either SDK. */
    static String checkout(PaymentGateway gateway, String orderId, long cents, String currency) {
        PaymentResult r = gateway.charge(orderId, cents, currency);
        return r.success() ? "paid " + orderId + " ref=" + r.reference() : "failed " + orderId + " (" + r.error() + ")";
    }

    public static void main(String[] args) {
        PaymentGateway bank = new LegacyBankAdapter(new LegacyBankSdk());
        PaymentGateway stripe = new StripeAdapter(new StripeLikeClient());
        List<String> results = List.of(
                checkout(bank, "A-1", 49_900, "INR"),
                checkout(bank, "A-2", 2_000_000, "INR"),
                checkout(bank, "A-3", 1_000, "USD"),
                checkout(stripe, "A-4", 1_000, "USD"),
                checkout(stripe, "A-5", 1_001, "USD"));
        results.forEach(System.out::println);
        check(results.get(0).startsWith("paid") && results.get(1).contains("LIMIT_EXCEEDED"), "legacy adapter");
        check(results.get(3).contains("ch_A-4") && results.get(4).contains("DECLINED"), "stripe adapter");
        System.out.println("PASS");
    }

    static void check(boolean ok, String what) { if (!ok) { System.out.println("FAILED: " + what); System.exit(1); } }
}
