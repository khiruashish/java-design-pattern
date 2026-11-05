package com.handbook.creational.singleton.tenant;

public class TenantDemo {

    public static void main(String[] args) {
        TenantConfig india = TenantConfig.forTenant("IN");
        TenantConfig germany = TenantConfig.forTenant("DE");
        TenantConfig indiaAgain = TenantConfig.forTenant("IN");

        System.out.println(india.tenantId() + " -> " + india.currency());
        System.out.println(germany.tenantId() + " -> " + germany.currency());
        System.out.println("Same IN instance? " + (india == indiaAgain));
        System.out.println("IN and DE differ? " + (india != germany));
    }
}
