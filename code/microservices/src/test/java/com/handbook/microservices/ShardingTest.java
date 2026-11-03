package com.handbook.microservices;

import com.handbook.microservices.sharding.banking.ShardedLedger;
import com.handbook.microservices.sharding.geo.GeoShardDirectory;
import com.handbook.microservices.sharding.scatter.ScatterGather;
import com.handbook.microservices.sharding.scatter.ScatterGather.Shard;
import com.handbook.microservices.sharding.scatter.ScatterGather.SkuSales;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class ShardingTest {

    @Test
    void routingIsStableAndLocalTransfersAreAtomic() {
        ShardedLedger l = new ShardedLedger(3);
        assertThat(l.shardOf("ACC-1")).isEqualTo(l.shardOf("ACC-1"));
        l.open("ACC-1001", 100);
        l.open("ACC-1004", 0);
        assertThat(l.shardOf("ACC-1001")).isEqualTo(l.shardOf("ACC-1004"));
        l.transfer("ACC-1001", "ACC-1004", 40);
        assertThat(l.balance("ACC-1001") + l.balance("ACC-1004")).isEqualTo(100);
    }

    @Test
    void splittingAHotCityMovesOnlyThatCity() {
        GeoShardDirectory d = new GeoShardDirectory();
        d.assign("a", "s1");
        d.assign("b", "s1");
        d.split("a", 50, "s2");
        assertThat(d.route("a", 49)).isEqualTo("s1");
        assertThat(d.route("a", 50)).isEqualTo("s2");
        assertThat(d.route("b", 99)).isEqualTo("s1");
    }

    @Test
    void scatterGatherFindsTheTrueGlobalTop() {
        List<Shard> shards = List.of(new Shard("0", List.of(new SkuSales("A", 10), new SkuSales("C", 9))),
                new Shard("1", List.of(new SkuSales("B", 10), new SkuSales("C", 9))));
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            assertThat(ScatterGather.globalTopN(shards, 1, pool)).containsExactly(new SkuSales("C", 18));
        }
    }
}
