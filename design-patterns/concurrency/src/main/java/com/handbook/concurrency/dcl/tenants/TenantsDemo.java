package com.handbook.concurrency.dcl.tenants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TenantsDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS: One Search Index per Tenant, Created on First Request ===");
        TenantResources resources = new TenantResources();
        String[] tenants = {"acme", "globex", "initech"};
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3_000; i++) {
                String tenant = tenants[i % 3];
                ex.submit(() -> resources.indexFor(tenant));
            }
        }
        System.out.println("Requests        : 3000 across 3 tenants");
        System.out.println("Indexes created : " + resources.created());
        System.out.println("Same instance on repeat call: " + (resources.indexFor("acme") == resources.indexFor("acme")));
    }
}
