package com.handbook.creational.singleton.tenant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Coding-challenge solution: one config object per tenant (a "Multiton").
 */
public final class TenantConfig {

    private static final Map<String, TenantConfig> INSTANCES = new ConcurrentHashMap<>();

    private final String tenantId;
    private final String currency;

    private TenantConfig(String tenantId) {
        this.tenantId = tenantId;
        this.currency = switch (tenantId) {
            case "IN" -> "INR";
            case "DE" -> "EUR";
            default -> "USD";
        };
        System.out.println("[TenantConfig] Loaded tenant " + tenantId);
    }

    public static TenantConfig forTenant(String tenantId) {
        return INSTANCES.computeIfAbsent(tenantId, TenantConfig::new); // atomic per key
    }

    public String tenantId() {
        return tenantId;
    }

    public String currency() {
        return currency;
    }
}
