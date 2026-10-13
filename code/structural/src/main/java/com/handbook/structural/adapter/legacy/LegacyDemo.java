package com.handbook.structural.adapter.legacy;

import java.util.List;
import java.util.Vector;

public class LegacyDemo {

    public static void main(String[] args) {
        Vector<String> legacyQueue = new Vector<>(List.of("INV-1", "INV-2", "INV-3")); // old API returns Enumeration
        for (String invoice : EnumerationIterator.iterable(legacyQueue.elements())) {
            System.out.println("Processing " + invoice);
        }
    }
}
