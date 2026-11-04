package com.handbook.testing.contract.messages;

import com.handbook.testing.contract.messages.SchemaCompatibility.ConsumerNeeds;
import com.handbook.testing.contract.messages.SchemaCompatibility.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentEventDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Evolving the payments.payment.captured.v1 Event Safely ===");
        List<ConsumerNeeds> consumers = List.of(
                new ConsumerNeeds("ledger", Map.of("paymentId", "string", "amountPaise", "long", "currency", "string")),
                new ConsumerNeeds("notifications", Map.of("paymentId", "string", "customerId", "string")),
                new ConsumerNeeds("analytics", Map.of("paymentId", "string", "amountPaise", "long")));

        Map<String, Field> v1 = schema("amountPaise", new Field("long", true), null, null);
        Map<String, Field> v2AddOptional = schema("amountPaise", new Field("long", true), "method", new Field("string", false));
        Map<String, Field> v3Breaking = new LinkedHashMap<>(schema("amount", new Field("decimal", true), null, null));

        report("v1 (today)", v1, consumers);
        report("v2 adds optional 'method'", v2AddOptional, consumers);
        report("v3 'amountPaise' -> 'amount' as decimal", v3Breaking, consumers);
        System.out.println("Breaking changes go to a NEW topic/version (...captured.v2), published alongside v1 during migration.");
    }

    static Map<String, Field> schema(String amountName, Field amount, String extraName, Field extra) {
        Map<String, Field> s = new LinkedHashMap<>();
        s.put("paymentId", new Field("string", true));
        s.put("customerId", new Field("string", true));
        s.put(amountName, amount);
        s.put("currency", new Field("string", true));
        if (extraName != null) {
            s.put(extraName, extra);
        }
        return s;
    }

    static void report(String label, Map<String, Field> schema, List<ConsumerNeeds> consumers) {
        List<String> problems = SchemaCompatibility.check(schema, consumers);
        System.out.println(label + " -> " + (problems.isEmpty() ? "compatible with all 3 consumers" : "BREAKING: " + problems));
    }
}
