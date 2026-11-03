package com.handbook.microservices.healthcheck.probes;

import java.util.function.BooleanSupplier;

/** Liveness: "am I broken beyond repair?" Readiness: "can I serve traffic right now?" They answer different questions. */
public final class GroceryProbes {

    private final BooleanSupplier eventLoopResponsive;
    private final BooleanSupplier databaseReachable;
    private final BooleanSupplier catalogCacheWarm;

    public GroceryProbes(BooleanSupplier eventLoopResponsive, BooleanSupplier databaseReachable, BooleanSupplier catalogCacheWarm) {
        this.eventLoopResponsive = eventLoopResponsive;
        this.databaseReachable = databaseReachable;
        this.catalogCacheWarm = catalogCacheWarm;
    }

    public int liveness() { // (1)!
        return eventLoopResponsive.getAsBoolean() ? 200 : 503;
    }

    public int readiness() { // (2)!
        return eventLoopResponsive.getAsBoolean() && databaseReachable.getAsBoolean() && catalogCacheWarm.getAsBoolean() ? 200 : 503;
    }

    /** What the platform does with the two answers. */
    public static String kubernetesAction(int liveness, int readiness) {
        if (liveness != 200) {
            return "RESTART container";
        }
        return readiness == 200 ? "send traffic" : "remove from Service endpoints (no restart)";
    }
}
