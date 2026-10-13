package com.handbook.behavioral.state.trip;

import com.handbook.behavioral.state.trip.Trip.Event;
import com.handbook.behavioral.state.trip.Trip.State;

import java.util.List;

public class TripDemo {

    static State run(String title, List<Event> events) {
        System.out.println(title);
        State s = new State.Searching();
        for (Event e : events) {
            try {
                s = Trip.next(s, e);
                System.out.println("  " + e + " -> " + s);
            } catch (IllegalStateException ex) {
                System.out.println("  " + e + " rejected: " + ex.getMessage());
            }
        }
        return s;
    }

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: Trip State Machine ===");
        run("Normal trip:", List.of(new Event.Assign("Ramesh"), new Event.Arrive(), new Event.Start(), new Event.Finish(8)));
        run("Rider cancels after a long wait:", List.of(new Event.Assign("Sunita"), new Event.Arrive(), new Event.Wait(7), new Event.RiderCancels()));
        run("Bug in client app:", List.of(new Event.Start(), new Event.Assign("Imran"), new Event.Finish(3)));
    }
}
