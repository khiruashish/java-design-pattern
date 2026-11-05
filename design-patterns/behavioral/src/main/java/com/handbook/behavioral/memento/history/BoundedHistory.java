package com.handbook.behavioral.memento.history;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/** Challenge solution: a caretaker that keeps only the newest N mementos. */
public class BoundedHistory<M> {

    private final int capacity;
    private final Deque<M> stack = new ArrayDeque<>();
    private int dropped;

    public BoundedHistory(int capacity) {
        this.capacity = capacity;
    }

    public void push(M memento) {
        if (stack.size() == capacity) {
            stack.removeLast(); // forget the oldest
            dropped++;
        }
        stack.push(memento);
    }

    public Optional<M> pop() {
        return Optional.ofNullable(stack.poll());
    }

    public int size() { return stack.size(); }

    public int dropped() { return dropped; }
}
