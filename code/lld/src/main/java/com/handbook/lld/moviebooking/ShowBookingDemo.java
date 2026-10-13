package com.handbook.lld.moviebooking;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ShowBookingDemo {

    public static void main(String[] args) throws InterruptedException {
        var show = new ShowBooking(List.of("A1", "A2", "A3", "A4"), Duration.ofMinutes(5));
        Instant t = Instant.parse("2026-10-14T18:00:00Z");

        System.out.println("=== LLD: Movie ticket booking with seat holds ===");
        System.out.println("Asha holds A1,A2   : " + show.hold("asha", List.of("A1", "A2"), t));
        System.out.println("Ravi holds A2,A3   : " + show.hold("ravi", List.of("A2", "A3"), t) + " (all-or-nothing)");
        System.out.println("Ravi holds A3      : " + show.hold("ravi", List.of("A3"), t));
        System.out.println("seat map           : " + show.seatMap(t) + "  (h = held, X = booked)");
        System.out.println("Asha pays at +2 min: " + show.confirm("asha", List.of("A1", "A2"), t.plusSeconds(120)));
        System.out.println("Ravi pays at +6 min: " + show.confirm("ravi", List.of("A3"), t.plusSeconds(360)) + " (hold expired, refund)");
        System.out.println("seat map at +6 min : " + show.seatMap(t.plusSeconds(360)));

        var race = new ShowBooking(List.of("B7"), Duration.ofMinutes(5));
        AtomicInteger winners = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        Thread[] users = new Thread[50];
        for (int i = 0; i < users.length; i++) {
            String user = "user-" + i;
            users[i] = Thread.ofVirtual().start(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (race.hold(user, List.of("B7"), t)) {
                    winners.incrementAndGet();
                }
            });
        }
        start.countDown();
        for (Thread u : users) {
            u.join();
        }
        System.out.println("50 users race for seat B7 -> holds granted: " + winners.get());
    }
}
