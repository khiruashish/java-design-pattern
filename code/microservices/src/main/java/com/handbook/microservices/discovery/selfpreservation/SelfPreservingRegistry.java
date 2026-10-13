package com.handbook.microservices.discovery.selfpreservation;

import com.handbook.microservices.discovery.registry.ServiceRegistry;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

/**
 * Coding challenge (Eureka's "self-preservation"): if a large share of leases expire at once, the
 * registry is probably cut off from healthy instances by a network partition. Evicting them all would
 * leave callers with nothing, so it stops evicting until heartbeats recover.
 */
public final class SelfPreservingRegistry extends ServiceRegistry {

    private final double maxEvictionRatio;
    private boolean preserving;

    public SelfPreservingRegistry(Clock clock, Duration leaseTtl, double maxEvictionRatio) {
        super(clock, leaseTtl);
        this.maxEvictionRatio = maxEvictionRatio;
    }

    @Override
    public List<String> evictExpired() {
        int expired = expiredIds().size();
        int total = registeredCount();
        preserving = total > 0 && (double) expired / total > maxEvictionRatio;
        if (preserving) {
            System.out.printf("    self-preservation ON: %d of %d leases expired at once, evicting nobody%n",
                    expired, total);
            return List.of();
        }
        return super.evictExpired();
    }

    @Override
    public List<Instance> lookup(String service) {
        return preserving ? lookupIncludingExpired(service) : super.lookup(service); // stale beats empty
    }
}
