package com.handbook.enterprise.aggregate.cinema;

import java.util.Map;
import java.util.TreeMap;

/** A small aggregate: one show's seats. Customers and payments are referenced by ID only. */
public final class Show {

    public record ShowId(String value) { }

    public record CustomerId(String value) { }

    private final ShowId id;
    private final int capacity;
    private final Map<String, CustomerId> bookedSeats = new TreeMap<>(); // seat -> customer id (1)!

    public Show(ShowId id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public void book(CustomerId customer, String... seats) { // (2)!
        for (String seat : seats) {
            if (bookedSeats.containsKey(seat)) {
                throw new IllegalStateException("seat " + seat + " already taken; nothing booked");
            }
        }
        if (bookedSeats.size() + seats.length > capacity) {
            throw new IllegalStateException("show " + id.value() + " is full");
        }
        for (String seat : seats) {
            bookedSeats.put(seat, customer);
        }
    }

    public Map<String, CustomerId> bookedSeats() { return Map.copyOf(bookedSeats); }

    @Override
    public String toString() {
        return id.value() + " " + bookedSeats.keySet() + " (" + bookedSeats.size() + "/" + capacity + ")";
    }
}
