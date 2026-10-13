package com.handbook.microservices.sidecar.telemetry;

import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.time.Clock;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Records golden-signal metrics (traffic, errors, latency) for every request, for any app in any
 * language, without changing the app. Also starts a trace if the request has none.
 */
public final class TelemetrySidecar {

    private record Stats(int count, int errors, long totalMillis, long maxMillis) {
    }

    private final Function<Request, Response> app;
    private final Clock clock;
    private final Map<String, Stats> byRoute = new TreeMap<>();
    private int nextTrace = 1;

    public TelemetrySidecar(Function<Request, Response> app, Clock clock) {
        this.app = app;
        this.clock = clock;
    }

    public Response handle(Request request) {
        Request traced = request.header("traceparent") != null ? request // (1)!
                : request.withHeader("traceparent", String.format("00-%032x-%016x-01", nextTrace, nextTrace++));
        long start = clock.millis();
        Response response = app.apply(traced);
        long took = clock.millis() - start;
        String route = request.method() + " " + request.path().replaceAll("/[A-Z]+-\\d+", "/{id}"); // (2)!
        byRoute.merge(route, new Stats(1, response.status() >= 500 ? 1 : 0, took, took),
                (a, b) -> new Stats(a.count() + 1, a.errors() + b.errors(), a.totalMillis() + b.totalMillis(),
                        Math.max(a.maxMillis(), b.maxMillis())));
        return response;
    }

    public void printMetrics() {
        byRoute.forEach((route, s) -> System.out.printf("  %-22s requests=%d errors=%d avg=%dms max=%dms%n",
                route, s.count(), s.errors(), s.totalMillis() / s.count(), s.maxMillis()));
    }
}
