package com.handbook.behavioral.chain.middleware;

import java.util.List;
import java.util.Map;

/** Functional chain, like servlet filters / Spring WebFilter: each step may stop or continue. */
@FunctionalInterface
public interface Middleware {

    record Request(String path, Map<String, String> headers, String clientIp) {
    }

    record Response(int status, String body) {
    }

    interface Next {
        Response proceed(Request request);
    }

    Response handle(Request request, Next next);

    static Next chain(List<Middleware> middlewares, Next endpoint) { // (1)!
        Next current = endpoint;
        for (int i = middlewares.size() - 1; i >= 0; i--) {
            Middleware m = middlewares.get(i);
            Next downstream = current;
            current = req -> m.handle(req, downstream);
        }
        return current;
    }
}
