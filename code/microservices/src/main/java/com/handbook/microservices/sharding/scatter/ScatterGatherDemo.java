package com.handbook.microservices.sharding.scatter;

import com.handbook.microservices.sharding.scatter.ScatterGather.Shard;
import com.handbook.microservices.sharding.scatter.ScatterGather.SkuSales;

import java.util.List;
import java.util.concurrent.Executors;

public class ScatterGatherDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: 'Top 2 SKUs Today' Across Customer-Sharded Order Databases ===");
        List<Shard> shards = List.of(
                new Shard("orders-0", List.of(new SkuSales("MILK", 50), new SkuSales("BREAD", 40), new SkuSales("EGGS", 39))),
                new Shard("orders-1", List.of(new SkuSales("ATTA", 60), new SkuSales("MILK", 45), new SkuSales("EGGS", 44))),
                new Shard("orders-2", List.of(new SkuSales("BREAD", 55), new SkuSales("ATTA", 52), new SkuSales("EGGS", 50))));
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("Scatter-gather of full totals : " + ScatterGather.globalTopN(shards, 2, pool));
        }
        System.out.println("Merging each shard's top 2    : " + ScatterGather.mergeOfLocalTopN(shards, 2)
                + "  <- EGGS (133 units, the real #1) missed: never in a local top 2");
        System.out.println("A query with the shard key (one customer's orders) would touch exactly 1 of 3 shards.");
    }
}
