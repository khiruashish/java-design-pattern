package com.handbook.concurrency.producerconsumer.buffer;

/** Challenge solution: the classic bounded buffer with intrinsic locks, wait() and notifyAll(). */
public class BoundedBuffer<T> {

    private final Object[] items;
    private int head;
    private int tail;
    private int count;

    public BoundedBuffer(int capacity) {
        items = new Object[capacity];
    }

    public synchronized void put(T item) throws InterruptedException {
        while (count == items.length) { // ALWAYS a loop: spurious wakeups and competing producers
            wait();
        }
        items[tail] = item;
        tail = (tail + 1) % items.length;
        count++;
        notifyAll(); // wake consumers (and producers; notifyAll avoids lost wakeups)
    }

    @SuppressWarnings("unchecked")
    public synchronized T take() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        T item = (T) items[head];
        items[head] = null; // let GC reclaim it
        head = (head + 1) % items.length;
        count--;
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return count;
    }
}
