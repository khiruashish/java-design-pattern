package com.handbook.microservices.tracing.core;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/** A tiny tracer: creates spans, records them when they end, and prints a trace as a tree. */
public final class Tracer {

    public record Span(String traceId, String spanId, String parentId, String service, String name,
                       long startMillis, long endMillis) {
        long duration() {
            return endMillis - startMillis;
        }
    }

    private final Clock clock;
    private final Random ids;
    private final List<Span> finished = new ArrayList<>(); // what a collector (Jaeger, Tempo) receives

    public Tracer(Clock clock, Random ids) {
        this.clock = clock;
        this.ids = ids;
    }

    /** Runs work inside a new span. {@code parent} may be null to start a new trace. */
    public <T> T inSpan(TraceContext parent, String service, String name,
                        Function<TraceContext, T> work) {
        TraceContext ctx = parent == null ? TraceContext.newRoot(ids) : parent.newChild(ids);
        long start = clock.millis();
        try {
            return work.apply(ctx);
        } finally {
            synchronized (finished) {
                finished.add(new Span(ctx.traceId(), ctx.spanId(), parent == null ? null : parent.spanId(),
                        service, name, start, clock.millis()));
            }
        }
    }

    public <T> T inSpan(TraceContext parent, String service, String name, Supplier<T> work) {
        return inSpan(parent, service, name, ctx -> work.get());
    }

    public List<Span> spans() {
        synchronized (finished) {
            return List.copyOf(finished);
        }
    }

    public void printTree(String traceId) {
        List<Span> trace = spans().stream().filter(s -> s.traceId().equals(traceId)).toList();
        long t0 = trace.stream().mapToLong(Span::startMillis).min().orElse(0);
        long t1 = trace.stream().mapToLong(Span::endMillis).max().orElse(t0);
        double msPerChar = Math.max(1, (t1 - t0) / 40.0); // the whole trace fits in 40 characters
        trace.stream().filter(s -> s.parentId() == null).forEach(root -> print(root, trace, t0, msPerChar, 0));
    }

    private void print(Span span, List<Span> trace, long t0, double msPerChar, int depth) {
        int offset = (int) ((span.startMillis() - t0) / msPerChar);
        int width = (int) Math.max(1, Math.round(span.duration() / msPerChar));
        System.out.printf("  %-50s %5d ms |%s%n", "  ".repeat(depth) + span.service() + ": " + span.name(),
                span.duration(), " ".repeat(offset) + "#".repeat(width));
        trace.stream().filter(s -> span.spanId().equals(s.parentId()))
                .sorted(Comparator.comparingLong(Span::startMillis))
                .forEach(child -> print(child, trace, t0, msPerChar, depth + 1));
    }
}
