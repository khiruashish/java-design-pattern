package com.handbook.microservices.saga.travel;

import java.util.Set;

public class TripBookingDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: flight + hotel + cab, with a pivot step ===");
        new TripBookingSaga(new FakePartners(Set.of(), 0)).book("TRIP-1");

        System.out.println();
        System.out.println("-- hotel is sold out: cancel the flight hold --");
        new TripBookingSaga(new FakePartners(Set.of("hotel"), 0)).book("TRIP-2");

        System.out.println();
        System.out.println("-- after payment, the cab partner is flaky: retry forward, don't refund --");
        new TripBookingSaga(new FakePartners(Set.of(), 2)).book("TRIP-3");
    }

    /** Fake partner APIs: some always fail, and the cab API fails its first N calls. */
    private static final class FakePartners implements TripBookingSaga.Partners {
        private final Set<String> failing;
        private int cabFailuresLeft;

        FakePartners(Set<String> failing, int cabFailures) {
            this.failing = failing;
            this.cabFailuresLeft = cabFailures;
        }

        private void maybeFail(String partner, String reason) {
            if (failing.contains(partner)) {
                throw new IllegalStateException(reason);
            }
        }

        @Override public void holdFlight(String trip) { maybeFail("flight", "no seats"); }
        @Override public void cancelFlight(String trip) { }
        @Override public void holdHotel(String trip) { maybeFail("hotel", "sold out"); }
        @Override public void cancelHotel(String trip) { }
        @Override public void chargeCard(String trip) { maybeFail("card", "card declined"); }
        @Override public void sendItinerary(String trip) { }

        @Override
        public void bookAirportCab(String trip) {
            if (cabFailuresLeft-- > 0) {
                throw new IllegalStateException("cab API timeout");
            }
        }
    }
}
