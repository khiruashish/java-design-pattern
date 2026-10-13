package com.handbook.microservices.gateway.grocery;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;

public class MobileGatewayDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Mobile API Gateway ===");
        MobileGateway gateway = new MobileGateway()
                .route("/catalog", r -> Response.of(200, "catalog-service saw " + r.path()), false)
                .route("/cart", r -> Response.of(200, "cart-service: cart of " + r.header("X-Customer-Id")), true)
                .route("/orders", r -> Response.of(200, "order-service: orders of " + r.header("X-Customer-Id") + " at " + r.path()), true);

        Map<String, String> loggedIn = Map.of("Authorization", "Bearer tok-asha");
        System.out.println("GET /catalog/products/MANGO   -> " + gateway.handle(Request.get("/catalog/products/MANGO", Map.of())));
        System.out.println("GET /cart (no token)          -> " + gateway.handle(Request.get("/cart", Map.of())));
        System.out.println("GET /cart (logged in)         -> " + gateway.handle(Request.get("/cart", loggedIn)));
        System.out.println("GET /orders/ORD-9 (logged in) -> " + gateway.handle(Request.get("/orders/ORD-9", loggedIn)));
        System.out.println("GET /admin/users              -> " + gateway.handle(Request.get("/admin/users", loggedIn)));
    }
}
