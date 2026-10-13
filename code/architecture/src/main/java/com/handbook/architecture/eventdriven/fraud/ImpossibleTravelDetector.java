package com.handbook.architecture.eventdriven.fraud;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Complex event processing: look for a PATTERN across events for the same card. Two transactions in
 * different cities within a short time is "impossible travel", a classic fraud signal.
 */
public final class ImpossibleTravelDetector {

    public record CardSwiped(String card, String city, Instant at, long paise) {
    }

    private final Duration window;
    private final Map<String, CardSwiped> lastByCard = new HashMap<>(); // (1)!

    public ImpossibleTravelDetector(Duration window) {
        this.window = window;
    }

    public Optional<String> onEvent(CardSwiped e) {
        CardSwiped previous = lastByCard.put(e.card(), e);
        if (previous != null && !previous.city().equals(e.city())
                && Duration.between(previous.at(), e.at()).compareTo(window) < 0) { // (2)!
            return Optional.of("FRAUD ALERT " + e.card() + ": " + previous.city() + " then " + e.city()
                    + " within " + Duration.between(previous.at(), e.at()).toMinutes() + " min");
        }
        return Optional.empty();
    }
}
