package com.handbook.microservices;

import com.handbook.microservices.backpressure.flow.BatchingSubscriber;
import com.handbook.microservices.backpressure.flow.GpsPublisher;
import com.handbook.microservices.backpressure.overflow.IntakeBuffer;
import com.handbook.microservices.backpressure.overflow.IntakeBuffer.Policy;
import com.handbook.microservices.backpressure.shedding.ConcurrencyLimiter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BackpressureTest {

    @Test
    void subscriberControlsTheRate() {
        BatchingSubscriber s = new BatchingSubscriber(2);
        new GpsPublisher(List.of("a", "b", "c")).subscribe(s);
        assertThat(s.log()).containsExactly("request(2)", "map-match [a, b] -> request(2)", "complete, last partial batch [c]");
    }

    @Test
    void fullBufferAppliesItsPolicy() {
        IntakeBuffer reject = new IntakeBuffer(1, Policy.REJECT_NEW);
        reject.offer("a");
        assertThat(reject.offer("b")).startsWith("429");
        IntakeBuffer drop = new IntakeBuffer(1, Policy.DROP_OLDEST);
        drop.offer("a");
        drop.offer("b");
        assertThat(drop.contents()).containsExactly("b");
    }

    @Test
    void limiterAdmitsWhenPermitsAreFree() {
        assertThat(new ConcurrencyLimiter(1).handle(() -> "ok")).isEqualTo("ok");
    }
}
