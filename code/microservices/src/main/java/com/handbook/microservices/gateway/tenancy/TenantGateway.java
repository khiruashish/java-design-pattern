package com.handbook.microservices.gateway.tenancy;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/** Multi-tenant SaaS: resolve the tenant from the Host header and route to its regional shard. */
public class TenantGateway {

    private final Map<String, String> tenantToShard;
    private final Map<String, Function<Request, Response>> shards;
    private final Set<String> shardsDown;

    public TenantGateway(Map<String, String> tenantToShard, Map<String, Function<Request, Response>> shards, Set<String> shardsDown) {
        this.tenantToShard = tenantToShard;
        this.shards = shards;
        this.shardsDown = shardsDown;
    }

    public Response handle(Request req) {
        String host = req.header("Host");                       // acme.erp.example.com
        String tenant = host == null ? "" : host.split("\\.")[0]; // (1)!
        String shard = tenantToShard.get(tenant);
        if (shard == null) {
            return Response.of(404, "unknown tenant '" + tenant + "'");
        }
        if (shardsDown.contains(shard)) {
            return Response.of(503, "shard " + shard + " under maintenance").withHeader("Retry-After", "120"); // (2)!
        }
        return shards.get(shard).apply(req.withHeader("X-Tenant", tenant)); // (3)!
    }
}
