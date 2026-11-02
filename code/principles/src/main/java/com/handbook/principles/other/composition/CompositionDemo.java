package com.handbook.principles.other.composition;

import java.util.List;
import java.util.TreeSet;

public class CompositionDemo {
    public static void main(String[] args) {
        InstrumentedHashSet<String> inherited = new InstrumentedHashSet<>();
        inherited.addAll(List.of("a", "b", "c"));
        System.out.println("inheritance: added 3, addCount = " + inherited.addCount() + "  <-- double counted");

        InstrumentedSet<String> composed = new InstrumentedSet<>(new TreeSet<>());
        composed.addAll(List.of("a", "b", "c"));
        System.out.println("composition: added 3, addCount = " + composed.addCount() + "  (wrapping a TreeSet: " + composed + ")");
    }
}
