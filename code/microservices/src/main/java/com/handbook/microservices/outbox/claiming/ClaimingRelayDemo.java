package com.handbook.microservices.outbox.claiming;

import com.handbook.microservices.support.Db;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimingRelayDemo {

    public static void main(String[] args) throws Exception {
        DataSource db = Db.inMemory("claiming",
                "CREATE TABLE outbox (id BIGINT AUTO_INCREMENT PRIMARY KEY, claimed_by VARCHAR)",
                "INSERT INTO outbox (claimed_by) SELECT NULL FROM SYSTEM_RANGE(1, 500)");

        Map<Long, AtomicInteger> publishCount = new ConcurrentHashMap<>();
        CountDownLatch start = new CountDownLatch(1);
        Thread[] relays = new Thread[3];
        for (int r = 0; r < relays.length; r++) {
            ClaimingRelay relay = new ClaimingRelay(db, "relay-" + r);
            relays[r] = Thread.ofPlatform().start(() -> {
                try {
                    start.await();
                    while (relay.pollOnce(20, id -> publishCount.computeIfAbsent(id, k -> new AtomicInteger())
                            .incrementAndGet()) > 0) {
                        // keep polling until nothing is left
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            });
        }
        start.countDown();
        for (Thread t : relays) {
            t.join();
        }

        System.out.println("=== Challenge: 3 relay instances, 500 outbox rows ===");
        System.out.println("Rows published      : " + publishCount.size());
        System.out.println("Published twice     : " + publishCount.values().stream().filter(c -> c.get() > 1).count());
        System.out.println("Every row claimed   : " + (Db.count(db, "SELECT COUNT(*) FROM outbox WHERE claimed_by IS NULL") == 0));
    }
}
