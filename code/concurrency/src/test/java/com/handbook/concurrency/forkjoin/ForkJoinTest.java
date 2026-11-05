package com.handbook.concurrency.forkjoin;

import com.handbook.concurrency.forkjoin.masking.MaskIdentifiers;
import com.handbook.concurrency.forkjoin.routes.RouteSearch;
import com.handbook.concurrency.forkjoin.sales.SalesTotal;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

class ForkJoinTest {

    @Test
    void forkJoinSumEqualsSequentialSum() {
        long[] values = LongStream.rangeClosed(1, 1_000_000).toArray();
        assertThat(ForkJoinPool.commonPool().invoke(new SalesTotal(values, 0, values.length))).isEqualTo(500_000_500_000L);
    }

    @Test
    void maskingTouchesOnlyRecordsWithIds() {
        String[] r = {"id=123456789012", "no id", "id=999988887777"};
        LongAdder n = new LongAdder();
        ForkJoinPool.commonPool().invoke(new MaskIdentifiers(r, 0, r.length, n));
        assertThat(n.sum()).isEqualTo(2);
        assertThat(r[0]).isEqualTo("id=XXXX-XXXX-9012");
        assertThat(r[1]).isEqualTo("no id");
    }

    @Test
    void routeSearchFindsTheOptimumOnASmallGraph() {
        int[][] km = {{0, 1, 10, 10}, {1, 0, 1, 10}, {10, 1, 0, 1}, {10, 10, 1, 0}};
        RouteSearch.Route best = ForkJoinPool.commonPool().invoke(new RouteSearch(km, List.of("A", "B", "C", "D"), List.of(0), 0));
        assertThat(best.stops()).containsExactly("A", "B", "C", "D");
        assertThat(best.km()).isEqualTo(3);
    }
}
