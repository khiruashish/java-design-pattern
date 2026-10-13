package com.handbook.lld.lrucache;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LRU cache with O(1) get and put: a HashMap for lookup plus a doubly linked list for recency
 * (most recent at the head). A single lock makes it thread-safe; shard it for high concurrency.
 */
public final class LruCache<K, V> {

    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> index = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null); // sentinels avoid null checks
    private final Node<K, V> tail = new Node<>(null, null);
    private final ReentrantLock lock = new ReentrantLock();
    private int evictions;

    public LruCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be >= 1");
        }
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock();
        try {
            Node<K, V> n = index.get(key);
            if (n == null) {
                return null;
            }
            moveToHead(n);
            return n.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            Node<K, V> n = index.get(key);
            if (n != null) {
                n.value = value;
                moveToHead(n);
                return;
            }
            if (index.size() == capacity) {
                Node<K, V> lru = tail.prev;
                unlink(lru);
                index.remove(lru.key);
                evictions++;
            }
            n = new Node<>(key, value);
            index.put(key, n);
            linkAtHead(n);
        } finally {
            lock.unlock();
        }
    }

    private void moveToHead(Node<K, V> n) {
        unlink(n);
        linkAtHead(n);
    }

    private void unlink(Node<K, V> n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void linkAtHead(Node<K, V> n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }

    public int evictions() {
        return evictions;
    }

    /** Most recently used first. */
    @Override
    public String toString() {
        lock.lock();
        try {
            StringJoiner j = new StringJoiner(", ", "[", "]");
            for (Node<K, V> n = head.next; n != tail; n = n.next) {
                j.add(n.key + "=" + n.value);
            }
            return j.toString();
        } finally {
            lock.unlock();
        }
    }
}
