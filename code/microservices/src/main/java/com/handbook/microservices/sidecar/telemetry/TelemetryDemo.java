package com.handbook.microservices.sidecar.telemetry;

import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;

public class TelemetryDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T08:00:00Z");
        // A legacy fare service (imagine it's written in PHP): no metrics, no tracing.
        var sidecar = new TelemetrySidecar(request -> {
            boolean surge = request.path().contains("surge");
            clock.advanceMillis(surge ? 180 : 40);
            if (request.path().endsWith("TRIP-13")) {
                return Response.of(500, "fare table missing");
            }
            return Response.of(200, "fare ok, trace-id ..." + request.header("traceparent").substring(27, 35));
        }, clock);

        System.out.println("=== Ride-hailing: golden signals from a sidecar, zero app changes ===");
        for (String trip : new String[] {"TRIP-11", "TRIP-12", "TRIP-13"}) {
            System.out.println("GET /fares/" + trip + " -> " + sidecar.handle(Request.get("/fares/" + trip, Map.of())));
        }
        System.out.println("GET /fares/surge/TRIP-14 -> " + sidecar.handle(Request.get("/fares/surge/TRIP-14", Map.of())));
        System.out.println("Metrics exported to Prometheus:");
        sidecar.printMetrics();
    }
}
