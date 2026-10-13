package com.handbook.microservices.strangler.grocery;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The facade in front of a legacy monolith. Routes that have been rewritten go to new services;
 * everything else still goes to the monolith. Migration = moving one route at a time.
 */
public final class StranglerRouter {

    private final Function<Request, Response> legacy;
    private final Map<String, Function<Request, Response>> migrated = new LinkedHashMap<>();

    public StranglerRouter(Function<Request, Response> legacy) {
        this.legacy = legacy;
    }

    public void migrate(String pathPrefix, Function<Request, Response> newService) { // (1)!
        migrated.put(pathPrefix, newService);
    }

    public void rollback(String pathPrefix) { // (2)!
        migrated.remove(pathPrefix);
    }

    public Response handle(Request request) {
        return migrated.entrySet().stream()
                .filter(e -> request.path().startsWith(e.getKey()))
                .findFirst()
                .map(e -> e.getValue().apply(request))
                .orElseGet(() -> legacy.apply(request)); // (3)!
    }
}
