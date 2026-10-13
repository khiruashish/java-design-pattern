package com.handbook.microservices.tracing.sampling;

import com.handbook.microservices.tracing.core.TraceContext;

import java.util.Random;

public class SamplingDemo {

    public static void main(String[] args) {
        RatioSampler sampler = new RatioSampler(0.10);
        Random ids = new Random(2026);
        int sampled = 0;
        int disagreements = 0;
        for (int i = 0; i < 10_000; i++) {
            TraceContext root = TraceContext.newRoot(ids);
            boolean rootDecision = sampler.shouldSampleRoot(root.traceId());
            sampled += rootDecision ? 1 : 0;
            TraceContext sent = new TraceContext(root.traceId(), root.spanId(), rootDecision);
            boolean downstream = sampler.shouldSample(TraceContext.parse(sent.toTraceparent()));
            boolean recomputed = sampler.shouldSampleRoot(root.traceId()); // another service, same rule
            if (downstream != rootDecision || recomputed != rootDecision) {
                disagreements++;
            }
        }
        System.out.println("=== Challenge: consistent 10% trace sampling ===");
        System.out.println("traces sampled: " + sampled + " of 10,000");
        System.out.println("services disagreeing on a trace: " + disagreements + " (every sampled trace is complete)");
    }
}
