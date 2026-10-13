package com.handbook.microservices.strangler.insurance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Parallel run ("shadow traffic"): every quote is computed by both systems. The customer gets the
 * legacy answer; differences are logged for investigation. When mismatches reach zero, switch over.
 */
public final class ShadowComparator<Q> {

    private final Function<Q, Long> legacy;
    private final Function<Q, Long> modern;
    private final List<String> mismatches = new ArrayList<>();
    private int compared;

    public ShadowComparator(Function<Q, Long> legacy, Function<Q, Long> modern) {
        this.legacy = legacy;
        this.modern = modern;
    }

    public long quote(Q request) {
        long answer = legacy.apply(request); // (1)!
        try {
            long candidate = modern.apply(request); // (2)!
            compared++;
            if (candidate != answer) {
                mismatches.add(request + ": legacy=" + answer + " modern=" + candidate);
            }
        } catch (RuntimeException e) {
            mismatches.add(request + ": modern failed " + e.getMessage()); // (3)!
        }
        return answer;
    }

    public List<String> mismatches() {
        return List.copyOf(mismatches);
    }

    public int compared() {
        return compared;
    }
}
