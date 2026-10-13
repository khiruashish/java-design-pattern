package com.handbook.architecture.eventdriven.fraud;

import com.handbook.architecture.eventdriven.fraud.ImpossibleTravelDetector.CardSwiped;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class FraudDetectionDemo {

    public static void main(String[] args) {
        var detector = new ImpossibleTravelDetector(Duration.ofMinutes(60));
        Instant t = Instant.parse("2026-10-14T09:00:00Z");

        System.out.println("=== Banking: real-time fraud detection on card events ===");
        for (CardSwiped e : List.of(
                new CardSwiped("CARD-1", "Bengaluru", t, 45_000),
                new CardSwiped("CARD-2", "Mumbai", t.plusSeconds(60), 120_000),
                new CardSwiped("CARD-1", "Bengaluru", t.plusSeconds(600), 9_900),
                new CardSwiped("CARD-1", "Delhi", t.plusSeconds(1_500), 2_499_900),
                new CardSwiped("CARD-2", "Pune", t.plusSeconds(4 * 3_600), 30_000))) {
            String verdict = detector.onEvent(e).orElse("ok");
            System.out.printf("%s %-9s %s ₹%,d -> %s%n", e.card(), e.city(), e.at().toString().substring(11, 16),
                    e.paise() / 100, verdict);
        }
    }
}
