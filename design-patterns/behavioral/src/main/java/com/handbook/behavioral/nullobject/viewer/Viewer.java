package com.handbook.behavioral.nullobject.viewer;

import java.util.ArrayList;
import java.util.List;

/** A signed-in member or a guest. The player code treats both the same way. */
public interface Viewer {

    String greeting();

    List<String> recommendations();

    void recordWatched(String title);

    boolean canDownload();

    final class Member implements Viewer {
        private final String name;
        private final List<String> history = new ArrayList<>();

        public Member(String name) {
            this.name = name;
        }

        public String greeting() { return "Welcome back, " + name; }

        public List<String> recommendations() {
            return history.isEmpty() ? List.of("Trending: Monsoon Express")
                    : List.of("Because you watched " + history.getLast() + ": Season 2");
        }

        public void recordWatched(String title) { history.add(title); }

        public boolean canDownload() { return true; }
    }

    Viewer GUEST = new Viewer() { // (1)!
        public String greeting() { return "Hello! Sign in for personalised picks"; }
        public List<String> recommendations() { return List.of("Trending: Monsoon Express", "Top 10 in India"); }
        public void recordWatched(String title) { }   // nothing to remember
        public boolean canDownload() { return false; }
    };
}
