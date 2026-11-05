package com.handbook.creational.objectpool.travel;

import java.util.List;

public class TravelDemo {

    public static void main(String[] args) {
        System.out.println("=== Travel: Licensed GDS Session Pool (2 licences, refresh after 3 uses) ===");
        GdsSessionPool pool = new GdsSessionPool(2, 3);

        for (String route : List.of("BLR-DEL", "DEL-DXB", "BOM-LHR", "MAA-SIN", "HYD-BKK")) {
            String fares = pool.withSession(s -> s.search(route));
            System.out.println(fares);
        }

        System.out.println("-- a long search holds a session while another request arrives --");
        String nested = pool.withSession(outer -> {
            String first = outer.search("CCU-NRT");
            String second = pool.withSession(inner -> inner.search("GOI-DOH"));
            return first + " | " + second;
        });
        System.out.println(nested);
    }
}
