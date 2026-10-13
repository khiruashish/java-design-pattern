package com.handbook.microservices.discovery.clientside;

import com.handbook.microservices.discovery.registry.ServiceRegistry;
import com.handbook.microservices.discovery.registry.ServiceRegistry.Instance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

/**
 * Client-side discovery (Spring Cloud LoadBalancer style): the caller looks up instances,
 * picks one round-robin, and on a connection failure tries the next one.
 */
public final class LoadBalancedClient {

    private final ServiceRegistry registry;
    private final AtomicInteger next = new AtomicInteger();

    public LoadBalancedClient(ServiceRegistry registry) {
        this.registry = registry;
    }

    /** {@code call} sends the request to an address; it throws if the instance is unreachable. */
    public String call(String service, String request, BiFunction<String, String, String> call) {
        List<Instance> candidates = registry.lookup(service); // (1)!
        if (candidates.isEmpty()) {
            throw new IllegalStateException("no instances of " + service);
        }
        Set<String> tried = new HashSet<>();
        while (tried.size() < candidates.size()) {
            Instance pick = candidates.get(Math.floorMod(next.getAndIncrement(), candidates.size())); // (2)!
            if (!tried.add(pick.id())) {
                continue;
            }
            try {
                return call.apply(pick.address(), request);
            } catch (RuntimeException unreachable) { // (3)!
                System.out.println("    " + pick.address() + " unreachable, trying next instance");
            }
        }
        throw new IllegalStateException("all instances of " + service + " failed");
    }
}
