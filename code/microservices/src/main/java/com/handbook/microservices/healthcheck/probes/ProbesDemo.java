package com.handbook.microservices.healthcheck.probes;

public class ProbesDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Liveness vs Readiness Through a Pod's Life ===");
        boolean[] loop = {true};
        boolean[] db = {true};
        boolean[] cache = {false};
        GroceryProbes probes = new GroceryProbes(() -> loop[0], () -> db[0], () -> cache[0]);

        String[] phases = {"starting, cache cold", "cache warmed", "database failover (30s)", "database back", "thread deadlock"};
        for (String phase : phases) {
            switch (phase) {
                case "cache warmed" -> cache[0] = true;
                case "database failover (30s)" -> db[0] = false;
                case "database back" -> db[0] = true;
                case "thread deadlock" -> loop[0] = false;
                default -> { }
            }
            int live = probes.liveness();
            int ready = probes.readiness();
            System.out.printf("%-24s live=%d ready=%d -> %s%n", phase, live, ready, GroceryProbes.kubernetesAction(live, ready));
        }
    }
}
