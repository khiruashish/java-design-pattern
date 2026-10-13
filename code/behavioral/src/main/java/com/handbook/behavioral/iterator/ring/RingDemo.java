package com.handbook.behavioral.iterator.ring;

import java.util.ConcurrentModificationException;

public class RingDemo {

    public static void main(String[] args) {
        RingBuffer<String> lastPings = new RingBuffer<>(3); // last 3 GPS pings of a delivery bike
        for (String ping : new String[] {"12.9301,77.6201", "12.9305,77.6210", "12.9312,77.6222", "12.9320,77.6230"}) {
            lastPings.add(ping);
        }
        System.out.print("Last 3 pings:");
        for (String p : lastPings) {
            System.out.print(" [" + p + "]");
        }
        System.out.println();
        try {
            for (String p : lastPings) {
                lastPings.add("12.9333,77.6240"); // modifying while iterating
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Fail-fast: " + e.getMessage());
        }
    }
}
