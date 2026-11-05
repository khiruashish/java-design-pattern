package com.handbook.behavioral.iterator.feed;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/** Merges several newest-first sources into one newest-first feed (k-way merge), lazily. */
public class FeedMerger implements Iterator<FeedMerger.Post> {

    public record Post(long timestamp, String source, String text) {
    }

    private record Head(Post post, Iterator<Post> rest) {
    }

    private final PriorityQueue<Head> heap =
            new PriorityQueue<>(Comparator.comparingLong((Head h) -> h.post().timestamp()).reversed()); // (1)!

    public FeedMerger(List<Iterator<Post>> sources) {
        for (Iterator<Post> s : sources) {
            if (s.hasNext()) {
                heap.add(new Head(s.next(), s));
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !heap.isEmpty();
    }

    @Override
    public Post next() {
        Head top = heap.poll();
        if (top == null) {
            throw new NoSuchElementException();
        }
        if (top.rest().hasNext()) {
            heap.add(new Head(top.rest().next(), top.rest())); // (2)!
        }
        return top.post();
    }
}
