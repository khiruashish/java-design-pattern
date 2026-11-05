package com.handbook.behavioral.strategy.matching;

import com.handbook.behavioral.strategy.matching.MatchingStrategy.Driver;

import java.util.List;

public class MatchingDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Same Drivers, Different Matching Policies ===");
        List<Driver> available = List.of(
                new Driver("Ramesh", 0.6, 4.2, 3),
                new Driver("Sunita", 1.4, 4.9, 12),
                new Driver("Imran", 2.8, 4.7, 35));

        for (MatchingStrategy s : List.of(MatchingStrategy.nearest(),
                MatchingStrategy.bestRatedWithin(2.0), MatchingStrategy.fairness(3.0))) {
            System.out.printf("%-28s -> %s%n", s.name(), s.pick(available).map(Driver::name).orElse("no driver"));
        }
    }
}
