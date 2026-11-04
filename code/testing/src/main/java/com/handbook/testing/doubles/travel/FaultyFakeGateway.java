package com.handbook.testing.doubles.travel;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/** A FAKE with fault injection: behaves like the real hotel API but can be told to fail in specific ways. */
public final class FaultyFakeGateway implements BookingService.HotelApi {

    public enum Fault { TIMEOUT, SOLD_OUT }

    private final Deque<Fault> scripted = new ArrayDeque<>();
    private final Map<String, String> holds = new HashMap<>();
    private int calls;

    public FaultyFakeGateway failNext(Fault fault) { // (1)!
        scripted.add(fault);
        return this;
    }

    @Override
    public String hold(String hotel, String night) {
        calls++;
        Fault f = scripted.poll();
        if (f == Fault.TIMEOUT) {
            throw new IllegalStateException("timeout");
        }
        if (f == Fault.SOLD_OUT) {
            return null;
        }
        String id = "HOLD-" + calls;
        holds.put(id, hotel + "@" + night);
        return id;
    }

    @Override
    public void release(String holdId) {
        holds.remove(holdId);
    }

    public int calls() {
        return calls;
    }

    public Map<String, String> activeHolds() {
        return Map.copyOf(holds);
    }
}
