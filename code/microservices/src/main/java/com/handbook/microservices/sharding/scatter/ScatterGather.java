package com.handbook.microservices.sharding.scatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/** A query without the shard key must ask EVERY shard and merge the partial results. */
public final class ScatterGather {

    public record SkuSales(String sku, long units) { }

    /** One shard's local data: orders of the customers that hash to it. */
    public record Shard(String name, List<SkuSales> orderLines) {
        List<SkuSales> topN(int n) { // (1)!
            Map<String, Long> totals = new HashMap<>();
            orderLines.forEach(l -> totals.merge(l.sku(), l.units(), Long::sum));
            return totals.entrySet().stream().map(e -> new SkuSales(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparingLong(SkuSales::units).reversed().thenComparing(SkuSales::sku))
                    .limit(n).toList();
        }

        Map<String, Long> totals() {
            Map<String, Long> totals = new HashMap<>();
            orderLines.forEach(l -> totals.merge(l.sku(), l.units(), Long::sum));
            return totals;
        }
    }

    public static List<SkuSales> globalTopN(List<Shard> shards, int n, ExecutorService pool) {
        List<CompletableFuture<Map<String, Long>>> partials = new ArrayList<>();
        for (Shard s : shards) {
            partials.add(CompletableFuture.supplyAsync(s::totals, pool)); // (2)!
        }
        Map<String, Long> merged = new HashMap<>();
        partials.forEach(f -> f.join().forEach((sku, units) -> merged.merge(sku, units, Long::sum))); // (3)!
        return merged.entrySet().stream().map(e -> new SkuSales(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(SkuSales::units).reversed().thenComparing(SkuSales::sku))
                .limit(n).toList();
    }

    /** Wrong shortcut: merging each shard's local top-N can miss the true global top-N. */
    public static List<SkuSales> mergeOfLocalTopN(List<Shard> shards, int n) {
        Map<String, Long> merged = new HashMap<>();
        shards.forEach(s -> s.topN(n).forEach(x -> merged.merge(x.sku(), x.units(), Long::sum)));
        return merged.entrySet().stream().map(e -> new SkuSales(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(SkuSales::units).reversed().thenComparing(SkuSales::sku))
                .limit(n).toList();
    }
}
