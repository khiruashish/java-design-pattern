package com.handbook.behavioral.iterator.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/** One collection, several traversal strategies: each mode is a different Iterator. */
public class Playlist {

    public enum Mode { IN_ORDER, REPEAT_ALL, SHUFFLE }

    private final List<String> songs;

    public Playlist(List<String> songs) {
        this.songs = List.copyOf(songs);
    }

    public Iterator<String> play(Mode mode, long seed) {
        return switch (mode) {
            case IN_ORDER -> songs.iterator();
            case REPEAT_ALL -> new Iterator<>() { // (1)!
                private int i;
                public boolean hasNext() { return !songs.isEmpty(); }
                public String next() {
                    if (songs.isEmpty()) {
                        throw new NoSuchElementException();
                    }
                    return songs.get(i++ % songs.size());
                }
            };
            case SHUFFLE -> {
                List<String> order = new ArrayList<>(songs);
                Collections.shuffle(order, new Random(seed)); // (2)!
                yield order.iterator();
            }
        };
    }
}
