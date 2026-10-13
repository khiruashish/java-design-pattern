package com.handbook.lld.moviebooking;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Movie ticket booking (BookMyShow-style) LLD. Core problem: two people must never get the same
 * seat. Seats are HELD for a few minutes while the user pays, then CONFIRMED or released.
 * Holds are atomic per seat (ConcurrentHashMap.compute); expired holds are reclaimable.
 */
public final class ShowBooking {

    public enum Status { HELD, BOOKED }

    public record SeatState(Status status, String user, Instant holdExpires) {
    }

    private final Map<String, SeatState> seats = new ConcurrentHashMap<>();
    private final List<String> layout;
    private final Duration holdTime;

    public ShowBooking(List<String> layout, Duration holdTime) {
        this.layout = List.copyOf(layout);
        this.holdTime = holdTime;
    }

    /** Hold all requested seats or none (seats are always processed in sorted order to avoid deadlock-like partial holds). */
    public synchronized boolean hold(String user, List<String> wanted, Instant now) {
        for (String seat : wanted) {
            if (!layout.contains(seat) || !isFree(seat, now)) {
                return false;
            }
        }
        wanted.stream().sorted().forEach(seat -> seats.put(seat, new SeatState(Status.HELD, user, now.plus(holdTime))));
        return true;
    }

    public synchronized boolean confirm(String user, List<String> held, Instant now) {
        for (String seat : held) {
            SeatState s = seats.get(seat);
            if (s == null || s.status() != Status.HELD || !s.user().equals(user) || !now.isBefore(s.holdExpires())) {
                return false; // hold lost (expired or never ours): payment must be refunded
            }
        }
        held.forEach(seat -> seats.put(seat, new SeatState(Status.BOOKED, user, null)));
        return true;
    }

    private boolean isFree(String seat, Instant now) {
        SeatState s = seats.get(seat);
        return s == null || (s.status() == Status.HELD && !now.isBefore(s.holdExpires()));
    }

    public synchronized String seatMap(Instant now) {
        Map<String, String> view = new TreeMap<>();
        for (String seat : layout) {
            SeatState s = seats.get(seat);
            view.put(seat, s == null || isFree(seat, now) ? "." : s.status() == Status.BOOKED ? "X" : "h");
        }
        return view.toString();
    }
}
