package com.handbook.behavioral.iterator.playlist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlaylistDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Road Trip Playlist ===");
        Playlist playlist = new Playlist(List.of("Kesariya", "Lose Yourself", "Bohemian Rhapsody", "Tum Hi Ho"));

        for (Playlist.Mode mode : Playlist.Mode.values()) {
            Iterator<String> it = playlist.play(mode, 42);
            List<String> next6 = new ArrayList<>();
            while (it.hasNext() && next6.size() < 6) {
                next6.add(it.next());
            }
            System.out.printf("%-10s -> %s%n", mode, next6);
        }
    }
}
