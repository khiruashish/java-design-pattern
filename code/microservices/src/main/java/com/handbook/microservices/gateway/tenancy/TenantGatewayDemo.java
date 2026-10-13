package com.handbook.microservices.gateway.tenancy;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;
import java.util.Set;

public class TenantGatewayDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS ERP: Tenant-Aware Routing to Regional Shards ===");
        TenantGateway gw = new TenantGateway(
                Map.of("acme", "eu-1", "globex", "us-2", "initech", "us-2"),
                Map.of("eu-1", r -> Response.of(200, "eu-1 served invoices for " + r.header("X-Tenant")),
                        "us-2", r -> Response.of(200, "us-2 served invoices for " + r.header("X-Tenant"))),
                Set.of("us-2"));

        for (String host : new String[] {"acme.erp.example.com", "globex.erp.example.com", "hooli.erp.example.com"}) {
            System.out.printf("%-24s -> %s%n", host, gw.handle(Request.get("/invoices", Map.of("Host", host))));
        }
    }
}
