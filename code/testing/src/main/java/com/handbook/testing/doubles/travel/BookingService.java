package com.handbook.testing.doubles.travel;

/** Retries timeouts up to 3 times; reports sold-out immediately. */
public final class BookingService {

    public interface HotelApi {
        String hold(String hotel, String night);
        void release(String holdId);
    }

    private final HotelApi api;

    public BookingService(HotelApi api) {
        this.api = api;
    }

    public String book(String hotel, String night) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                String hold = api.hold(hotel, night);
                return hold == null ? "sold out" : "held " + hold + " after " + attempt + " attempt(s)";
            } catch (IllegalStateException timeout) {
                // retry
            }
        }
        return "gave up after 3 timeouts";
    }
}
