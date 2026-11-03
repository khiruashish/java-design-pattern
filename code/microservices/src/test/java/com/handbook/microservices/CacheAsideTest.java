package com.handbook.microservices;

import com.handbook.microservices.caching.catalog.ProductCatalog;
import com.handbook.microservices.caching.catalog.ProductCatalog.Product;
import com.handbook.microservices.caching.catalog.TtlCache;
import com.handbook.microservices.caching.singleflight.SingleFlightLoader;
import com.handbook.microservices.caching.writebehind.WriteBehindCounter;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CacheAsideTest {

    @Test
    void cacheAsideServesFromCacheAndInvalidatesOnWrite() {
        ProductCatalog c = new ProductCatalog(new TtlCache<>(FakeClock.at("2026-01-01T00:00:00Z")));
        c.seed(new Product("A", "a", 100));
        c.get("A");
        c.get("A");
        assertThat(c.dbReads()).isEqualTo(1);
        c.updatePrice("A", 90);
        assertThat(c.get("A").pricePaise()).isEqualTo(90);
    }

    @Test
    void entriesExpireAfterTtl() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        TtlCache<String> cache = new TtlCache<>(clock);
        cache.set("k", "v", Duration.ofSeconds(5));
        clock.advance(Duration.ofSeconds(5));
        assertThat(cache.get("k")).isEmpty();
    }

    @Test
    void sequentialMissesAfterFillHitTheCache() {
        AtomicInteger loads = new AtomicInteger();
        SingleFlightLoader<Integer> l = new SingleFlightLoader<>(k -> loads.incrementAndGet());
        l.get("x");
        l.get("x");
        assertThat(loads).hasValue(1);
    }

    @Test
    void writeBehindAggregatesDeltas() {
        WriteBehindCounter c = new WriteBehindCounter();
        for (int i = 0; i < 100; i++) {
            c.increment("h");
        }
        assertThat(c.flush()).isEqualTo(1);
        assertThat(c.database()).containsEntry("h", 100L);
    }
}
