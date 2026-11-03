package com.handbook.microservices;

import com.handbook.microservices.competingconsumers.groups.ConsumerGroup;
import com.handbook.microservices.competingconsumers.invoices.InvoiceWorkers;
import com.handbook.microservices.competingconsumers.visibility.VisibilityQueue;
import com.handbook.microservices.support.FakeClock;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CompetingConsumersTest {

    @Test
    void everyMessageIsProcessedExactlyOnceByTheGroup() throws Exception {
        InvoiceWorkers w = new InvoiceWorkers();
        for (int i = 0; i < 50; i++) {
            w.publish("O" + i);
        }
        w.runWith(3);
        assertThat(w.processedCount()).hasSize(50).allSatisfy((k, v) -> assertThat(v).isEqualTo(1));
        assertThat(w.maxParallel()).isBetween(1, 3);
    }

    @Test
    void unackedMessageReappearsAfterVisibilityTimeout() {
        FakeClock clock = FakeClock.at("2026-01-01T00:00:00Z");
        VisibilityQueue q = new VisibilityQueue(clock, Duration.ofSeconds(10));
        q.send("m", "x");
        q.receive();
        assertThat(q.receive()).isEmpty();
        clock.advance(Duration.ofSeconds(10));
        assertThat(q.receive()).get().extracting(VisibilityQueue.Message::receiveCount).isEqualTo(2);
    }

    @Test
    void partitionsAreSplitWithoutOverlap() {
        ConsumerGroup g = new ConsumerGroup(5);
        g.join("a");
        Map<String, List<Integer>> a = g.join("b");
        assertThat(a.get("a")).containsExactly(0, 1, 2);
        assertThat(a.get("b")).containsExactly(3, 4);
        assertThat(a.values().stream().flatMap(List::stream).collect(Collectors.toSet())).hasSize(5);
    }
}
