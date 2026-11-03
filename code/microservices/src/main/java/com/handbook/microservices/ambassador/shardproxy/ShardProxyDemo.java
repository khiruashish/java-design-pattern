package com.handbook.microservices.ambassador.shardproxy;

public class ShardProxyDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Cart Service Talks to localhost:6379, the Ambassador Shards ===");
        CacheShardProxy redis = new CacheShardProxy(3);
        for (int i = 1; i <= 9_000; i++) {
            redis.set("cart:CUST-" + i, "{items:" + (i % 7) + "}");
        }
        System.out.println("cart:CUST-42 -> " + redis.get("cart:CUST-42"));
        System.out.println("Operations per shard: " + redis.hitsPerShard());
        System.out.println("The cart service has no shard map, no cluster client, and no code change when shards are added.");
    }
}
