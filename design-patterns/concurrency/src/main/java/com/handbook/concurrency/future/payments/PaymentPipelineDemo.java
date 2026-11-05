package com.handbook.concurrency.future.payments;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Dependent async steps: validate -> fraud score -> charge. thenCompose chains steps that return futures. */
public class PaymentPipelineDemo {

    record Payment(String id, String card, int amountInr) {
    }

    static class FraudException extends RuntimeException {
        FraudException(String m) { super(m); }
    }

    private final ExecutorService io;

    PaymentPipelineDemo(ExecutorService io) {
        this.io = io;
    }

    CompletableFuture<Payment> validate(Payment p) {
        return CompletableFuture.supplyAsync(() -> {
            if (p.amountInr() <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            return p;
        }, io);
    }

    CompletableFuture<Integer> fraudScore(Payment p) {
        return CompletableFuture.supplyAsync(() -> p.card().endsWith("0000") ? 92 : 12, io);
    }

    CompletableFuture<String> charge(Payment p) {
        return CompletableFuture.supplyAsync(() -> "CHG-" + p.id(), io);
    }

    CompletableFuture<String> process(Payment p) {
        return validate(p)
                .thenCompose(valid -> fraudScore(valid).thenCompose(score -> { // (1)!
                    if (score > 80) {
                        throw new FraudException("fraud score " + score);
                    }
                    return charge(valid);
                }))
                .handle((chargeId, error) -> { // (2)!
                    if (error == null) {
                        return p.id() + ": CHARGED " + chargeId;
                    }
                    Throwable cause = error instanceof CompletionException ? error.getCause() : error; // (3)!
                    return p.id() + ": DECLINED (" + cause.getMessage() + ")";
                });
    }

    public static void main(String[] args) {
        System.out.println("=== Payments: Async Validation, Fraud Check and Charge ===");
        try (ExecutorService io = Executors.newVirtualThreadPerTaskExecutor()) {
            PaymentPipelineDemo pipeline = new PaymentPipelineDemo(io);
            for (Payment p : new Payment[] {
                    new Payment("PAY-1", "4111-1111", 2_499),
                    new Payment("PAY-2", "4111-0000", 54_000),
                    new Payment("PAY-3", "4111-2222", 0)}) {
                System.out.println(pipeline.process(p).join());
            }
        }
    }
}
