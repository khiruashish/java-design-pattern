package com.handbook.microservices.strangler.banking;

import java.util.Set;
import java.util.function.Function;

/**
 * Moves customers to the new statement service gradually. A customer's bucket comes from a hash of
 * their ID, so each customer consistently sees one system. Staff are always on the new system,
 * and a kill switch sends everyone back instantly.
 */
public final class PercentageRollout {

    private final Function<String, String> legacy;
    private final Function<String, String> modern;
    private final Set<String> internalStaff;
    private volatile int percent;
    private volatile boolean killSwitch;

    public PercentageRollout(Function<String, String> legacy, Function<String, String> modern, Set<String> internalStaff) {
        this.legacy = legacy;
        this.modern = modern;
        this.internalStaff = internalStaff;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public void setKillSwitch(boolean on) {
        this.killSwitch = on;
    }

    public String statement(String customerId) {
        if (killSwitch) { // (1)!
            return legacy.apply(customerId);
        }
        boolean useModern = internalStaff.contains(customerId) // (2)!
                || Math.floorMod(customerId.hashCode(), 100) < percent; // (3)!
        return (useModern ? modern : legacy).apply(customerId);
    }
}
