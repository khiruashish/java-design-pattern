package com.handbook.concurrency.future.flights;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Ask three airlines at once; give each 300 ms; show the cheapest answer that arrived. */
public class FlightQuotesDemo {

    record Quote(String airline, int fareInr) {
    }

    static Optional<Quote> callAirline(String airline, int fare, long latencyMs) {
        try {
            Thread.sleep(latencyMs); // network + airline system
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.of(new Quote(airline, fare));
    }

    public static void main(String[] args) {
        System.out.println("=== Travel: BLR -> DEL Fare Search ===");
        try (ExecutorService io = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Optional<Quote>>> calls = List.of(
                    quote(io, "SkyIndia", 5_840, 60),
                    quote(io, "BlueJet", 5_310, 120),
                    quote(io, "BudgetAir", 4_990, 2_000)); // very slow today

            CompletableFuture.allOf(calls.toArray(CompletableFuture[]::new)).join(); // (1)!
            calls.forEach(c -> System.out.println("  " + c.join().map(Quote::toString).orElse("BudgetAir: no answer within 300 ms")));

            Quote best = calls.stream().map(CompletableFuture::join).flatMap(Optional::stream)
                    .min(Comparator.comparingInt(Quote::fareInr)).orElseThrow();
            System.out.println("Cheapest shown to customer: " + best.airline() + " INR " + best.fareInr());
        }
    }

    static CompletableFuture<Optional<Quote>> quote(ExecutorService io, String airline, int fare, long latency) {
        return CompletableFuture.supplyAsync(() -> callAirline(airline, fare, latency), io) // (2)!
                .completeOnTimeout(Optional.empty(), 300, TimeUnit.MILLISECONDS);           // (3)!
    }
}
