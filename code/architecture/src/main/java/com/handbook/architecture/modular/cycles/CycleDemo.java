package com.handbook.architecture.modular.cycles;

public class CycleDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: detect module cycles, compute a build order ===");
        ModuleGraph healthy = new ModuleGraph()
                .dependsOn("ordering", "catalog", "customers")
                .dependsOn("billing", "customers")
                .dependsOn("shipping", "ordering");
        System.out.println("cycle: " + healthy.findCycle().map(Object::toString).orElse("none")
                + ", build order: " + healthy.buildOrder());

        ModuleGraph tangled = new ModuleGraph()
                .dependsOn("ordering", "catalog", "billing")
                .dependsOn("billing", "customers", "shipping")
                .dependsOn("shipping", "ordering");
        System.out.println("cycle: " + tangled.findCycle().map(c -> String.join(" -> ", c)).orElse("none")
                + "  (break it with an event or by moving the shared concept)");
    }
}
