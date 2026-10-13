package com.handbook.microservices.tracing.async;

import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.tracing.core.TraceContext;
import com.handbook.microservices.tracing.core.Tracer;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Traces cross asynchronous boundaries too: the producer puts traceparent into the message headers,
 * and the consumer, possibly seconds later on another machine, continues the same trace.
 */
public class KafkaTracePropagationDemo {

    record Message(String topic, Map<String, String> headers, String payload) {
    }

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T22:00:00Z");
        Tracer tracer = new Tracer(clock, new Random(11));
        Queue<Message> topic = new ArrayDeque<>();

        System.out.println("=== Ride-hailing: trip completion -> async billing, one trace ===");
        String traceId = tracer.inSpan(null, "trip-service", "POST /trips/TRIP-5/complete", ctx -> {
            clock.advanceMillis(25);
            tracer.inSpan(ctx, "trip-service", "kafka send trips.completed", producer -> {
                topic.add(new Message("trips.completed",
                        Map.of("traceparent", producer.toTraceparent()), "TripCompleted TRIP-5")); // (1)!
                clock.advanceMillis(4);
                return null;
            });
            return ctx.traceId();
        });

        clock.advanceMillis(2_000); // consumer lag
        Message m = topic.poll();
        TraceContext fromHeaders = TraceContext.parse(m.headers().get("traceparent")); // (2)!
        tracer.inSpan(fromHeaders, "billing-service", "kafka receive " + m.topic(), consumer -> {
            clock.advanceMillis(30);
            tracer.inSpan(consumer, "billing-service", "INSERT invoice", () -> {
                clock.advanceMillis(12);
                return null;
            });
            return null;
        });

        System.out.println("message headers: " + m.headers());
        tracer.printTree(traceId);
        System.out.println("billing-service joined trace " + traceId.substring(0, 8) + "... 2 s after the HTTP request ended.");
    }
}
