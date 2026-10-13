package com.handbook.microservices.ratelimit.distributed;

import java.time.Clock;

/**
 * One of several gateway instances behind a load balancer. A per-node counter would let a user get
 * N x the limit; a shared counter keyed by user and window enforces one global limit.
 */
public final class GatewayNode {

    private final String name;
    private final SharedCounterStore store;
    private final long limitPerMinute;
    private final Clock clock;
    private long localCount;

    public GatewayNode(String name, SharedCounterStore store, long limitPerMinute, Clock clock) {
        this.name = name;
        this.store = store;
        this.limitPerMinute = limitPerMinute;
        this.clock = clock;
    }

    public String handleShared(String user) {
        String key = "rl:" + user + ":" + clock.millis() / 60_000; // (1)!
        long count = store.incrementAndGet(key); // (2)!
        return name + (count <= limitPerMinute ? " 200" : " 429");
    }

    public String handleLocalOnly() {
        return name + (++localCount <= limitPerMinute ? " 200" : " 429"); // (3)!
    }
}
