package com.handbook.capstone.delivery;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Delivery module: slot booking with a pluggable [Strategy] for choosing the slot. */
public final class DeliveryService {

    public record Slot(LocalTime start, int capacity, boolean ecoFriendly) {
    }

    /** Strategy: which slot to give an order. */
    public interface SlotStrategy {
        Optional<Slot> choose(List<Slot> slotsWithCapacity);
    }

    public static SlotStrategy earliest() {
        return slots -> slots.stream().findFirst();
    }

    /** Prefer a slot when a van is already in the area (fewer trips), else the earliest. */
    public static SlotStrategy ecoFirst() {
        return slots -> slots.stream().filter(Slot::ecoFriendly).findFirst().or(() -> slots.stream().findFirst());
    }

    private final Map<Slot, Integer> booked = new LinkedHashMap<>();
    private final List<Slot> slots = new ArrayList<>();

    public DeliveryService slot(LocalTime start, int capacity, boolean eco) {
        Slot s = new Slot(start, capacity, eco);
        slots.add(s);
        booked.put(s, 0);
        return this;
    }

    public synchronized Slot book(SlotStrategy strategy) {
        List<Slot> open = slots.stream().filter(s -> booked.get(s) < s.capacity()).toList();
        Slot chosen = strategy.choose(open).orElseThrow(() -> new IllegalStateException("no delivery slots left today"));
        booked.merge(chosen, 1, Integer::sum);
        return chosen;
    }

    public synchronized void cancel(Slot slot) {
        booked.merge(slot, -1, Integer::sum);
    }
}
