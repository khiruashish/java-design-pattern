package com.handbook.enterprise.unitofwork.travel;

import java.util.HashMap;
import java.util.Map;

public class TravelIdentityMapDemo {

    /** A mutable booking, like a JPA entity. */
    static final class Booking {
        final String pnr;
        String seat;
        Booking(String pnr, String seat) { this.pnr = pnr; this.seat = seat; }
    }

    static final Map<String, String> SEATS_TABLE = new HashMap<>(Map.of("PNR-42", "14C"));
    static int dbReads = 0;

    static Booking loadFromDb(String pnr) {
        dbReads++;
        return new Booking(pnr, SEATS_TABLE.get(pnr));
    }

    public static void main(String[] args) {
        System.out.println("=== Travel: Identity Map in a Check-in Session ===");

        dbReads = 0;
        Booking forSeatMap = loadFromDb("PNR-42");    // seat-map widget
        Booking forBaggage = loadFromDb("PNR-42");    // baggage widget, same request
        forSeatMap.seat = "3A";                         // passenger upgrades
        System.out.println("Without map: reads=" + dbReads + ", same object=" + (forSeatMap == forBaggage)
                + ", baggage widget still sees seat " + forBaggage.seat + " (stale copy)");

        dbReads = 0;
        IdentityMap<String, Booking> session = new IdentityMap<>(TravelIdentityMapDemo::loadFromDb);
        Booking a = session.get("PNR-42");
        Booking b = session.get("PNR-42");
        a.seat = "3A";
        System.out.println("With map   : reads=" + dbReads + ", same object=" + (a == b) + ", baggage widget sees seat " + b.seat);
    }
}
