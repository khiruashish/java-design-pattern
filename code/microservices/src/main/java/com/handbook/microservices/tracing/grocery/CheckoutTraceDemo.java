package com.handbook.microservices.tracing.grocery;

import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.tracing.core.TraceContext;
import com.handbook.microservices.tracing.core.Tracer;

import java.util.Map;
import java.util.Random;

/**
 * One checkout request crosses four services. Each service reads the incoming traceparent header,
 * creates a child span, and passes its own span ID downstream. The collector stitches the tree.
 */
public class CheckoutTraceDemo {

    static FakeClock clock = FakeClock.at("2026-10-13T19:00:00Z");
    static Tracer tracer = new Tracer(clock, new Random(7));

    public static void main(String[] args) {
        System.out.println("=== Grocery: tracing one slow checkout across services ===");
        Request incoming = Request.get("/checkout", Map.of()); // no traceparent: the gateway starts the trace
        String traceId = tracer.inSpan(null, "api-gateway", "POST /checkout", gw -> {
            clock.advanceMillis(5);
            orderService(incoming.withHeader("traceparent", gw.toTraceparent())); // (1)!
            clock.advanceMillis(3);
            return gw.traceId();
        });
        System.out.println("trace-id " + traceId);
        tracer.printTree(traceId);
        System.out.println("The tree shows where the time went: payment-service waiting on the bank.");
    }

    static void orderService(Request req) {
        TraceContext parent = TraceContext.parse(req.header("traceparent")); // (2)!
        tracer.inSpan(parent, "order-service", "placeOrder", ctx -> {
            clock.advanceMillis(10);
            tracer.inSpan(ctx, "inventory-service", "reserve", () -> {
                clock.advanceMillis(40);
                return null;
            });
            paymentService(req.withHeader("traceparent", ctx.toTraceparent()));
            clock.advanceMillis(15);
            return null;
        });
    }

    static void paymentService(Request req) {
        TraceContext parent = TraceContext.parse(req.header("traceparent"));
        tracer.inSpan(parent, "payment-service", "charge", ctx -> {
            clock.advanceMillis(20);
            tracer.inSpan(ctx, "payment-service", "HTTP POST bank /authorize", () -> {
                clock.advanceMillis(1_400); // the slow external call
                return null;
            });
            return null;
        });
    }
}
