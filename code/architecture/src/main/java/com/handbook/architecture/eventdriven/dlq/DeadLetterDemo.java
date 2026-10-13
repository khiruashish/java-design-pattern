package com.handbook.architecture.eventdriven.dlq;

import java.util.ArrayList;
import java.util.List;

public class DeadLetterDemo {

    public static void main(String[] args) {
        List<String> invoiced = new ArrayList<>();
        int[] flakyFailures = {1};
        var consumer = new RetryingConsumer<String>(event -> {
            if (event.contains("amount=abc")) {
                throw new IllegalArgumentException("cannot parse amount"); // poison: will never succeed
            }
            if (event.startsWith("ORD-3") && flakyFailures[0]-- > 0) {
                throw new IllegalStateException("invoice DB timeout"); // transient: succeeds on retry
            }
            invoiced.add(event.substring(0, 5));
        }, 3);

        System.out.println("=== Challenge: retries + dead-letter queue for poison messages ===");
        consumer.consume(List.of("ORD-1 amount=1447", "ORD-2 amount=abc", "ORD-3 amount=129", "ORD-4 amount=399"));
        System.out.println("invoiced: " + invoiced);
        System.out.println("dead letters: " + consumer.deadLetters());
    }
}
