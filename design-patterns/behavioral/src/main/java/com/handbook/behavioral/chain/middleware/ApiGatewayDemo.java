package com.handbook.behavioral.chain.middleware;

import com.handbook.behavioral.chain.middleware.Middleware.Request;
import com.handbook.behavioral.chain.middleware.Middleware.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiGatewayDemo {

    public static void main(String[] args) {
        System.out.println("=== SaaS API: Middleware Chain ===");
        Map<String, Integer> requestsPerIp = new HashMap<>();

        Middleware logging = (req, next) -> {
            Response res = next.proceed(req);
            System.out.println("  [log] " + req.path() + " -> " + res.status());
            return res;
        };
        Middleware rateLimit = (req, next) -> {
            int count = requestsPerIp.merge(req.clientIp(), 1, Integer::sum);
            return count > 2 ? new Response(429, "Too Many Requests") : next.proceed(req); // (1)!
        };
        Middleware auth = (req, next) ->
                "Bearer good-token".equals(req.headers().get("Authorization"))
                        ? next.proceed(req) : new Response(401, "Unauthorized");

        var api = Middleware.chain(List.of(logging, rateLimit, auth),
                req -> new Response(200, "{\"invoices\": 3}"));

        Map<String, String> ok = Map.of("Authorization", "Bearer good-token");
        System.out.println(api.proceed(new Request("/invoices", ok, "10.0.0.1")));
        System.out.println(api.proceed(new Request("/invoices", Map.of(), "10.0.0.2")));
        System.out.println(api.proceed(new Request("/invoices", ok, "10.0.0.1")));
        System.out.println(api.proceed(new Request("/invoices", ok, "10.0.0.1")));
    }
}
