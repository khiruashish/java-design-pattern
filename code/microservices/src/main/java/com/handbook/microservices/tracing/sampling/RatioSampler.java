package com.handbook.microservices.tracing.sampling;

import com.handbook.microservices.tracing.core.TraceContext;

/**
 * Coding challenge: keep ~10% of traces, and make every service agree on WHICH traces, so sampled
 * traces are complete. The root decides from the trace ID; downstream services honour the sampled
 * flag in traceparent (OpenTelemetry's ParentBased(TraceIdRatioBased) sampler).
 */
public final class RatioSampler {

    private final long threshold;

    public RatioSampler(double ratio) {
        this.threshold = (long) (ratio * Long.MAX_VALUE);
    }

    /** Root decision: a deterministic function of the trace ID (its last 16 hex chars). */
    public boolean shouldSampleRoot(String traceId) {
        long lower = Long.parseUnsignedLong(traceId.substring(16), 16) >>> 1; // non-negative 63 bits
        return lower < threshold;
    }

    /** Downstream decision: follow the parent. */
    public boolean shouldSample(TraceContext parent) {
        return parent.sampled();
    }
}
