package com.handbook.lld.systemdesign;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Consistent hashing with virtual nodes: keys and nodes are hashed onto a ring; a key belongs to
 * the first node clockwise. Adding or removing a node moves only about 1/N of the keys, instead of
 * nearly all of them with {@code hash(key) % N}.
 */
public final class ConsistentHashRing {

    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash(node + "#" + i), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash(node + "#" + i));
        }
    }

    public String nodeFor(String key) {
        SortedMap<Long, String> tail = ring.tailMap(hash(key));
        return tail.isEmpty() ? ring.firstEntry().getValue() : tail.get(tail.firstKey());
    }

    /** First 8 bytes of MD5: stable across JVMs and well distributed (not for security). */
    static long hash(String s) {
        try {
            byte[] d = MessageDigest.getInstance("MD5").digest(s.getBytes(StandardCharsets.UTF_8));
            long h = 0;
            for (int i = 0; i < 8; i++) {
                h = (h << 8) | (d[i] & 0xff);
            }
            return h;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, Integer> distribution(ConsistentHashRing ring, int keys) {
        Map<String, Integer> counts = new TreeMap<>();
        for (int i = 0; i < keys; i++) {
            counts.merge(ring.nodeFor("cart:" + i), 1, Integer::sum);
        }
        return counts;
    }
}
