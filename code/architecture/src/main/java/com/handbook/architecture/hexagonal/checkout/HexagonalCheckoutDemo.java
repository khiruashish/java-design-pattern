package com.handbook.architecture.hexagonal.checkout;

import com.handbook.architecture.hexagonal.checkout.adapter.in.CliCheckoutAdapter;
import com.handbook.architecture.hexagonal.checkout.adapter.in.RestCheckoutAdapter;
import com.handbook.architecture.hexagonal.checkout.adapter.out.InMemoryOrderRepository;
import com.handbook.architecture.hexagonal.checkout.adapter.out.PayFastPaymentAdapter;
import com.handbook.architecture.hexagonal.checkout.adapter.out.SmsNotificationAdapter;
import com.handbook.architecture.hexagonal.checkout.domain.CheckoutService;

import java.util.Map;
import java.util.TreeMap;

public class HexagonalCheckoutDemo {

    public static void main(String[] args) {
        // Fake PayFast HTTP API: declines amounts over ₹5,000.
        var payFast = new PayFastPaymentAdapter(request -> {
            System.out.println("    PayFast API <- " + new TreeMap<>(request));
            boolean ok = Double.parseDouble(request.get("amount")) <= 5_000;
            return Map.of("status", ok ? "CAPTURED" : "DECLINED_LIMIT");
        });
        var repository = new InMemoryOrderRepository();
        var core = new CheckoutService(payFast, repository, new SmsNotificationAdapter()); // composition root

        var rest = new RestCheckoutAdapter(core);
        var cli = new CliCheckoutAdapter(core);

        System.out.println("=== Grocery: one core, two driving adapters, three driven adapters ===");
        System.out.println(rest.post(Map.of("customerId", "CUST-42", "phone", "+91-98450-11111", "totalRupees", "1447.00")));
        System.out.println(cli.run("order CUST-77 +91-98450-22222 899000"));
        System.out.println(rest.post(Map.of("customerId", "CUST-42", "phone", "+91-98450-11111", "totalRupees", "-5")));
        System.out.println("Orders saved: " + repository.count());
    }
}
