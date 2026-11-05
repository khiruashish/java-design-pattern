package com.handbook.creational.abstractfactory.cloud;

import java.util.Map;

public class MultiCloudDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS ERP: Invoice Archiving per Tenant Cloud ===");
        // Tenant -> cloud chosen once, from configuration
        Map<String, CloudFactory> tenants = Map.of(
                "acme-retail", new AwsFactory("ap-south-1"),
                "globex-pharma", new AzureFactory("globexprod"));

        archive("acme-retail", tenants.get("acme-retail"), "INV-1001", 120);
        archive("globex-pharma", tenants.get("globex-pharma"), "INV-2001", 85);
    }

    private static void archive(String tenant, CloudFactory cloud, String invoiceId, int sizeKb) {
        System.out.println("Tenant " + tenant + " (" + cloud.getClass().getSimpleName() + "):");
        new InvoiceArchiver(cloud).archive(invoiceId, sizeKb)
                .forEach(line -> System.out.println("  " + line));
    }
}
