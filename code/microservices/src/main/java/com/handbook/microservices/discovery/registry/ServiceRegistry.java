package com.handbook.microservices.discovery.registry;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A minimal service registry (think Eureka or Consul). Instances register, renew their lease with
 * heartbeats, and disappear when the lease expires, so a crashed instance stops receiving traffic.
 */
public class ServiceRegistry {

    public record Instance(String service, String id, String address, String zone, Instant lastHeartbeat) {
    }

    private final Map<String, Instance> instances = new ConcurrentHashMap<>();
    private final Clock clock;
    private final Duration leaseTtl;

    public ServiceRegistry(Clock clock, Duration leaseTtl) {
        this.clock = clock;
        this.leaseTtl = leaseTtl;
    }

    public void register(String service, String id, String address, String zone) { // (1)!
        instances.put(id, new Instance(service, id, address, zone, clock.instant()));
    }

    public void heartbeat(String id) { // (2)!
        instances.computeIfPresent(id, (k, i) ->
                new Instance(i.service(), i.id(), i.address(), i.zone(), clock.instant()));
    }

    public void deregister(String id) { // (3)!
        instances.remove(id);
    }

    /** Healthy instances of a service: those whose lease has not expired. */
    public List<Instance> lookup(String service) {
        Instant now = clock.instant();
        return instances.values().stream()
                .filter(i -> i.service().equals(service))
                .filter(i -> isLeaseValid(i, now)) // (4)!
                .sorted(Comparator.comparing(Instance::id))
                .toList();
    }

    /** Every registered instance of a service, including ones whose lease has expired. */
    protected List<Instance> lookupIncludingExpired(String service) {
        return instances.values().stream()
                .filter(i -> i.service().equals(service))
                .sorted(Comparator.comparing(Instance::id))
                .toList();
    }

    /** Removes expired instances; registries run this periodically. Returns the evicted IDs. */
    public List<String> evictExpired() {
        List<String> expired = expiredIds();
        expired.forEach(instances::remove);
        return expired;
    }

    protected List<String> expiredIds() {
        Instant now = clock.instant();
        return instances.values().stream()
                .filter(i -> !isLeaseValid(i, now))
                .map(Instance::id).sorted().toList();
    }

    protected int registeredCount() {
        return instances.size();
    }

    private boolean isLeaseValid(Instance i, Instant now) {
        return i.lastHeartbeat().plus(leaseTtl).isAfter(now);
    }
}
