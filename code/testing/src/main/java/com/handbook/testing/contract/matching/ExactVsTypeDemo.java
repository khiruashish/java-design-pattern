package com.handbook.testing.contract.matching;

import com.handbook.testing.contract.core.Contract;
import com.handbook.testing.contract.core.Contract.AnyInteger;
import com.handbook.testing.contract.core.Contract.AnyString;
import com.handbook.testing.contract.core.Contract.Exactly;
import com.handbook.testing.contract.core.Contract.Interaction;
import com.handbook.testing.contract.core.Contract.Response;

import java.util.Map;

/** Coding challenge: exact-value contracts break when the provider's test data changes; type matchers don't. */
public class ExactVsTypeDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: Exact Values vs Type Matchers in a Contract ===");
        Contract exact = new Contract("checkout", "pricing").uponReceiving(new Interaction("price of a SKU", "SKU exists",
                "/price/ATTA-5KG", 200, Map.of("sku", new Exactly("ATTA-5KG"), "pricePaise", new Exactly(28_500))));
        Contract byType = new Contract("checkout", "pricing").uponReceiving(new Interaction("price of a SKU", "SKU exists",
                "/price/ATTA-5KG", 200, Map.of("sku", new Exactly("ATTA-5KG"), "pricePaise", new AnyInteger(),
                "currency", new AnyString())));

        // The pricing team's test fixture changed the seeded price from 285.00 to 279.00. Nothing about the API changed.
        java.util.function.Function<String, Response> pricing = path ->
                new Response(200, Map.of("sku", "ATTA-5KG", "pricePaise", 27_900, "currency", "INR"));

        exact.verify(state -> { }, pricing).forEach(l -> System.out.println("exact values : " + l));
        byType.verify(state -> { }, pricing).forEach(l -> System.out.println("type matcher : " + l));
        System.out.println("Contract the SHAPE the consumer depends on; assert specific values in the consumer's own unit tests.");
    }
}
