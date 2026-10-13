package com.handbook.microservices.tracing.core;

import java.util.Random;

/**
 * The W3C Trace Context carried between services in the {@code traceparent} header:
 * {@code 00-<32 hex trace-id>-<16 hex parent span-id>-<flags>} (flags 01 = sampled).
 */
public record TraceContext(String traceId, String spanId, boolean sampled) {

    public static TraceContext parse(String traceparent) {
        String[] parts = traceparent.split("-"); // (1)!
        if (parts.length != 4 || !parts[0].equals("00") || parts[1].length() != 32 || parts[2].length() != 16) {
            throw new IllegalArgumentException("bad traceparent: " + traceparent);
        }
        return new TraceContext(parts[1], parts[2], parts[3].equals("01"));
    }

    public String toTraceparent() {
        return "00-" + traceId + "-" + spanId + "-" + (sampled ? "01" : "00");
    }

    public static TraceContext newRoot(Random ids) {
        return new TraceContext(hex(ids, 16), hex(ids, 8), true);
    }

    public TraceContext newChild(Random ids) { // (2)!
        return new TraceContext(traceId, hex(ids, 8), sampled);
    }

    static String hex(Random ids, int bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes; i++) {
            sb.append(String.format("%02x", ids.nextInt(256)));
        }
        return sb.toString();
    }
}
