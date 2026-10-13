package com.handbook.behavioral.iterator.feed;

import com.handbook.behavioral.iterator.feed.FeedMerger.Post;

import java.util.List;

public class FeedDemo {

    public static void main(String[] args) {
        System.out.println("=== Social Media: Home Feed from Three Sources ===");
        List<Post> friends = List.of(new Post(1009, "friends", "Riya posted holiday photos"),
                new Post(1004, "friends", "Kabir changed his profile picture"),
                new Post(1001, "friends", "Neel shared a recipe"));
        List<Post> groups = List.of(new Post(1008, "groups", "Pune Runners: Sunday 10K route"),
                new Post(1002, "groups", "Java India: Virtual threads meetup"));
        List<Post> pages = List.of(new Post(1006, "pages", "City Metro: new line opens"),
                new Post(1005, "pages", "Weather: heavy rain alert"),
                new Post(1003, "pages", "Library: new arrivals"));

        FeedMerger feed = new FeedMerger(List.of(friends.iterator(), groups.iterator(), pages.iterator()));
        for (int i = 0; i < 6 && feed.hasNext(); i++) { // the UI only asks for one screen
            Post p = feed.next();
            System.out.printf("  t=%d [%-7s] %s%n", p.timestamp(), p.source(), p.text());
        }
    }
}
