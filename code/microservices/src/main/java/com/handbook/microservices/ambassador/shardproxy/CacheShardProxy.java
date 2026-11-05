package com.handbook.microservices.ambassador.shardproxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Like twemproxy/Envoy Redis proxy: the app talks to ONE local endpoint; the ambassador picks the shard. */
public final class CacheShardProxy {

    private final List<Map<String, String>> shards;
    private final Map<Integer, Integer> hitsPerShard = new HashMap<>();

    public CacheShardProxy(int shardCount) {
        this.shards = new java.util.ArrayList<>();
        for (int i = 0; i < shardCount; i++) {
            shards.add(new HashMap<>());
        }
    }

    private int shardFor(String key) { // (1)!
        return Math.floorMod(key.hashCode(), shards.size());
    }

    public void set(String key, String value) {
        int s = shardFor(key);
        hitsPerShard.merge(s, 1, Integer::sum);
        shards.get(s).put(key, value);
    }

    public String get(String key) {
        int s = shardFor(key);
        hitsPerShard.merge(s, 1, Integer::sum);
        return shards.get(s).get(key);
    }

    public Map<Integer, Integer> hitsPerShard() {
        return new java.util.TreeMap<>(hitsPerShard);
    }
}
