package com.handbook.microservices.gateway.grocery;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/** Single entry point for the mobile app: routing, authentication, prefix stripping, request IDs. */
public class MobileGateway {

    private record Route(String prefix, Function<Request, Response> service, boolean requiresLogin) {
    }

    private final Map<String, Route> routes = new LinkedHashMap<>();
    private final Map<String, String> sessions = Map.of("tok-asha", "CUST-42"); // token -> customer
    private int requestCounter;

    public MobileGateway route(String prefix, Function<Request, Response> service, boolean requiresLogin) {
        routes.put(prefix, new Route(prefix, service, requiresLogin));
        return this;
    }

    public Response handle(Request req) {
        String requestId = "req-" + (++requestCounter);
        Route route = routes.values().stream()
                .filter(r -> req.path().startsWith(r.prefix())).findFirst().orElse(null); // (1)!
        if (route == null) {
            return Response.of(404, "no route for " + req.path()).withHeader("X-Request-Id", requestId);
        }
        Request forwarded = req.withPath(req.path().substring(route.prefix().length())) // (2)!
                .withHeader("X-Request-Id", requestId);
        if (route.requiresLogin()) {
            String auth = req.header("Authorization");
            String customer = auth == null ? null : sessions.get(auth.replace("Bearer ", ""));
            if (customer == null) {
                return Response.of(401, "login required").withHeader("X-Request-Id", requestId); // (3)!
            }
            forwarded = forwarded.withHeader("X-Customer-Id", customer); // (4)!
        }
        return route.service().apply(forwarded).withHeader("X-Request-Id", requestId);
    }
}
