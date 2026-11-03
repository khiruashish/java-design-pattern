package com.handbook.enterprise.aggregate.cinema;

import com.handbook.enterprise.aggregate.cinema.Show.CustomerId;
import com.handbook.enterprise.aggregate.cinema.Show.ShowId;

public class ShowDemo {

    public static void main(String[] args) {
        System.out.println("=== Entertainment: Booking Seats in a Show Aggregate ===");
        Show show = new Show(new ShowId("STARLIGHT-AUDI3-2026-05-01-21:00"), 4);
        show.book(new CustomerId("C-1"), "F7", "F8");
        System.out.println("C-1 booked      -> " + show);
        try {
            show.book(new CustomerId("C-2"), "F9", "F8"); // F8 is taken
        } catch (IllegalStateException e) {
            System.out.println("C-2 F9+F8       -> " + e.getMessage() + " -> " + show);
        }
        show.book(new CustomerId("C-2"), "F9", "F10");
        System.out.println("C-2 F9+F10      -> " + show);
        try {
            show.book(new CustomerId("C-3"), "A1");
        } catch (IllegalStateException e) {
            System.out.println("C-3 A1          -> " + e.getMessage());
        }
    }
}
