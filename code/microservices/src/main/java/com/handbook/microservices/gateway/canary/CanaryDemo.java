package com.handbook.microservices.gateway.canary;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;

public class CanaryDemo {

    public static void main(String[] args) {
        CanaryRouter router = new CanaryRouter(r -> Response.of(200, "checkout v1"), r -> Response.of(200, "checkout v2"), 10);
        int v2 = 0;
        for (int i = 0; i < 1_000; i++) {
            if (router.handle(Request.get("/checkout", Map.of("X-User-Id", "user-" + i))).body().endsWith("v2")) {
                v2++;
            }
        }
        System.out.println("Users routed to v2: " + v2 + " of 1000 (target ~10%)");
        System.out.println("user-7 twice: " + router.handle(Request.get("/checkout", Map.of("X-User-Id", "user-7"))).body()
                + ", " + router.handle(Request.get("/checkout", Map.of("X-User-Id", "user-7"))).body() + " (sticky)");
        System.out.println("QA with header: " + router.handle(Request.get("/checkout", Map.of("X-User-Id", "qa", "X-Canary", "true"))).body());
    }
}
