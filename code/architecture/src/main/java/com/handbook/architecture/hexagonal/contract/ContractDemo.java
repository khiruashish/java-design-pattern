package com.handbook.architecture.hexagonal.contract;

import com.handbook.architecture.hexagonal.checkout.adapter.out.PayFastPaymentAdapter;
import com.handbook.architecture.hexagonal.checkout.port.out.PaymentPort;

import java.util.Map;

public class ContractDemo {

    public static void main(String[] args) {
        PaymentPort payFast = new PayFastPaymentAdapter(req ->
                Map.of("status", Double.parseDouble(req.get("amount")) <= 5_000 ? "CAPTURED" : "DECLINED_LIMIT"));
        PaymentPort fake = (customer, paise, ref) -> paise <= 500_000;
        PaymentPort buggyFake = (customer, paise, ref) -> paise < 500_000; // off-by-one at the boundary

        System.out.println("=== Challenge: one contract test suite for every PaymentPort adapter ===");
        PaymentPortContract.verify("PayFastAdapter", payFast).forEach(System.out::println);
        PaymentPortContract.verify("InMemoryFake", fake).forEach(System.out::println);
        PaymentPortContract.verify("BuggyFake", buggyFake).forEach(System.out::println);
    }
}
