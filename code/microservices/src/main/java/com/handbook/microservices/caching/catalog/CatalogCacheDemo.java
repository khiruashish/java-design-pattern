package com.handbook.microservices.caching.catalog;

import com.handbook.microservices.caching.catalog.ProductCatalog.Product;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class CatalogCacheDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Cache-Aside for Product Pages ===");
        FakeClock clock = FakeClock.at("2026-06-01T18:00:00Z");
        ProductCatalog catalog = new ProductCatalog(new TtlCache<>(clock));
        catalog.seed(new Product("BANANA-6", "Robusta Banana (6)", 4_200));

        for (int i = 0; i < 1_000; i++) {
            catalog.get("BANANA-6"); // evening rush on the product page
        }
        System.out.println("1,000 page views -> DB reads: " + catalog.dbReads());

        catalog.updatePrice("BANANA-6", 3_900);
        long paise = catalog.get("BANANA-6").pricePaise();
        System.out.printf("After price drop, next view shows INR %d.%02d (DB reads: %d)%n", paise / 100, paise % 100, catalog.dbReads());

        clock.advance(Duration.ofMinutes(11));
        catalog.get("BANANA-6");
        System.out.println("11 minutes later the entry expired and was reloaded (DB reads: " + catalog.dbReads() + ")");
    }
}
