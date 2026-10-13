package com.handbook.microservices.discovery.zone;

import com.handbook.microservices.discovery.registry.ServiceRegistry;
import com.handbook.microservices.discovery.registry.ServiceRegistry.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Prefer instances in the caller's availability zone (lower latency, no cross-zone data charges),
 * but spill over to all zones when the local zone has too few healthy instances.
 */
public final class ZoneAwareBalancer {

    private final ServiceRegistry registry;
    private final String myZone;
    private final int minLocalInstances;
    private final AtomicInteger counter = new AtomicInteger();

    public ZoneAwareBalancer(ServiceRegistry registry, String myZone, int minLocalInstances) {
        this.registry = registry;
        this.myZone = myZone;
        this.minLocalInstances = minLocalInstances;
    }

    public Instance choose(String service) {
        List<Instance> all = registry.lookup(service);
        List<Instance> local = all.stream().filter(i -> i.zone().equals(myZone)).toList(); // (1)!
        List<Instance> pool = local.size() >= minLocalInstances ? local : all; // (2)!
        if (pool.isEmpty()) {
            throw new IllegalStateException("no instances of " + service);
        }
        return pool.get(Math.floorMod(counter.getAndIncrement(), pool.size()));
    }
}
