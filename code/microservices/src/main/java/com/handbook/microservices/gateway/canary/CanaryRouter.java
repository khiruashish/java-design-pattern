package com.handbook.microservices.gateway.canary;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.function.Function;

/** Challenge solution: send a stable 10% of users (plus anyone with X-Canary: true) to v2. */
public class CanaryRouter {

    private final Function<Request, Response> stable;
    private final Function<Request, Response> canary;
    private final int canaryPercent;

    public CanaryRouter(Function<Request, Response> stable, Function<Request, Response> canary, int canaryPercent) {
        this.stable = stable;
        this.canary = canary;
        this.canaryPercent = canaryPercent;
    }

    public Response handle(Request req) {
        boolean forced = "true".equals(req.header("X-Canary"));
        String user = req.header("X-User-Id");
        boolean bucket = user != null && Math.floorMod(user.hashCode(), 100) < canaryPercent; // sticky per user
        return (forced || bucket ? canary : stable).apply(req);
    }
}
