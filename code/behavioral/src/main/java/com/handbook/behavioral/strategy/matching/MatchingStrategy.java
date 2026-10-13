package com.handbook.behavioral.strategy.matching;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Strategy: how to pick a driver for a rider. Ops can switch it per city. */
public interface MatchingStrategy {

    record Driver(String name, double kmAway, double rating, int idleMinutes) {
    }

    Optional<Driver> pick(List<Driver> available);

    String name();

    static MatchingStrategy nearest() {
        return new MatchingStrategy() {
            public Optional<Driver> pick(List<Driver> d) { return d.stream().min(Comparator.comparingDouble(Driver::kmAway)); }
            public String name() { return "nearest"; }
        };
    }

    static MatchingStrategy bestRatedWithin(double maxKm) {
        return new MatchingStrategy() {
            public Optional<Driver> pick(List<Driver> d) {
                return d.stream().filter(x -> x.kmAway() <= maxKm).max(Comparator.comparingDouble(Driver::rating));
            }
            public String name() { return "best-rated within " + maxKm + " km"; }
        };
    }

    static MatchingStrategy fairness(double maxKm) {
        return new MatchingStrategy() {
            public Optional<Driver> pick(List<Driver> d) {
                return d.stream().filter(x -> x.kmAway() <= maxKm).max(Comparator.comparingInt(Driver::idleMinutes));
            }
            public String name() { return "longest-idle within " + maxKm + " km"; }
        };
    }
}
