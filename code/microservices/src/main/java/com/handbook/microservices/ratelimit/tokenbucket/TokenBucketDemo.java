package com.handbook.microservices.ratelimit.tokenbucket;

import com.handbook.microservices.support.FakeClock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T09:00:00Z");
        Map<String, TokenBucket> perUser = new ConcurrentHashMap<>();

        System.out.println("=== Grocery: checkout API, 5-request burst, refills 1 token/second per user ===");
        StringBuilder burst = new StringBuilder();
        for (int i = 1; i <= 7; i++) {
            burst.append(allowed(perUser, clock, "CUST-42") ? "200 " : "429 ");
        }
        System.out.println("CUST-42 sends 7 requests at once : " + burst.toString().trim());
        System.out.println("CUST-77 is unaffected            : " + (allowed(perUser, clock, "CUST-77") ? "200" : "429"));

        clock.advanceMillis(2_500);
        System.out.println("2.5 s later, CUST-42 has " + bucket(perUser, clock, "CUST-42").tokensLeft() + " tokens");
        System.out.println("CUST-42 next 3 requests          : "
                + (allowed(perUser, clock, "CUST-42") ? "200 " : "429 ")
                + (allowed(perUser, clock, "CUST-42") ? "200 " : "429 ")
                + (allowed(perUser, clock, "CUST-42") ? "200" : "429"));

        clock.advanceMillis(60_000);
        System.out.println("After a quiet minute the bucket is full again (capped): "
                + bucket(perUser, clock, "CUST-42").tokensLeft() + " tokens");
    }

    private static TokenBucket bucket(Map<String, TokenBucket> perUser, FakeClock clock, String user) {
        return perUser.computeIfAbsent(user, u -> new TokenBucket(5, 1.0, clock));
    }

    private static boolean allowed(Map<String, TokenBucket> perUser, FakeClock clock, String user) {
        return bucket(perUser, clock, user).tryAcquire();
    }
}
