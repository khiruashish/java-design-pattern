package com.handbook.behavioral.iterator.ring;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Challenge solution: keeps the last N items; iterates oldest -> newest; fail-fast. */
public class RingBuffer<T> implements Iterable<T> {

    private final Object[] items;
    private int start;
    private int size;
    private int modCount;

    public RingBuffer(int capacity) {
        items = new Object[capacity];
    }

    public void add(T item) {
        int end = (start + size) % items.length;
        items[end] = item;
        if (size < items.length) {
            size++;
        } else {
            start = (start + 1) % items.length; // overwrite the oldest
        }
        modCount++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index;
            private final int expectedModCount = modCount;

            public boolean hasNext() {
                return index < size;
            }

            @SuppressWarnings("unchecked")
            public T next() {
                if (modCount != expectedModCount) {
                    throw new ConcurrentModificationException("buffer changed during iteration");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (T) items[(start + index++) % items.length];
            }
        };
    }
}
