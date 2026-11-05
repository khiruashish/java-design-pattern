package com.handbook.concurrency.forkjoin.routes;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class RoutesDemo {

    public static void main(String[] args) {
        System.out.println("=== Logistics: Cheapest Order of 7 Stops After the Hub (7! = 5,040 routes) ===");
        List<String> stops = List.of("Hub", "Koramangala", "HSR", "BTM", "Jayanagar", "JP Nagar", "Bellandur", "Indiranagar");
        int[][] km = new int[stops.size()][stops.size()];
        for (int i = 0; i < km.length; i++) {
            for (int j = 0; j < km.length; j++) {
                km[i][j] = i == j ? 0 : 2 + Math.abs(i * 7 - j * 5) % 11 + Math.abs(i - j);
            }
        }
        try (ForkJoinPool pool = new ForkJoinPool(4)) {
            RouteSearch.Route best = pool.invoke(new RouteSearch(km, stops, List.of(0), 0));
            System.out.println("Best route: " + String.join(" -> ", best.stops()));
            System.out.println("Distance  : " + best.km() + " km");
        }
    }
}
