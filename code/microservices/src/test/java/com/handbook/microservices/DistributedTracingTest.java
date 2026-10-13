package com.handbook.microservices;

import com.handbook.microservices.tracing.core.TraceContext;
import com.handbook.microservices.tracing.sampling.RatioSampler;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistributedTracingTest {

    @Test
    void traceparentRoundTripsAndChildKeepsTraceId() {
        TraceContext root = TraceContext.newRoot(new Random(1));
        TraceContext parsed = TraceContext.parse(root.toTraceparent());
        assertThat(parsed).isEqualTo(root);
        TraceContext child = parsed.newChild(new Random(2));
        assertThat(child.traceId()).isEqualTo(root.traceId());
        assertThat(child.spanId()).isNotEqualTo(root.spanId());
    }

    @Test
    void rejectsMalformedHeaders() {
        assertThatThrownBy(() -> TraceContext.parse("00-abc-def-01")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void samplingIsDeterministicPerTraceId() {
        RatioSampler sampler = new RatioSampler(0.5);
        String traceId = TraceContext.newRoot(new Random(9)).traceId();
        assertThat(sampler.shouldSampleRoot(traceId)).isEqualTo(sampler.shouldSampleRoot(traceId));
    }
}
