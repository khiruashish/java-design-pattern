package com.handbook.structural.proxy.streaming;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Home Screen with 40 Titles ===");
        List<Poster> row = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            row.add(new LazyPoster("Title " + i));
        }
        System.out.println("Home screen built with " + row.size() + " posters (nothing downloaded yet)");

        System.out.println("User sees the first 3 tiles:");
        row.subList(0, 3).forEach(p -> System.out.println("  " + p.render()));
        System.out.println("User scrolls back to tile 1:");
        System.out.println("  " + row.get(0).render() + "  <- no second download");
        System.out.println("Search index uses titles only: " + row.get(39).title());
    }
}
