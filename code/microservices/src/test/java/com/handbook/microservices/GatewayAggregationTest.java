package com.handbook.microservices;

import com.handbook.microservices.aggregation.budget.QuoteAggregator;
import com.handbook.microservices.aggregation.core.SimCall;
import com.handbook.microservices.aggregation.dag.DependencyAggregator;
import com.handbook.microservices.aggregation.dag.DependencyAggregator.Step;
import com.handbook.microservices.aggregation.fields.FieldDrivenAggregator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayAggregationTest {

    @Test
    void onlyBackendsOwningRequestedFieldsAreCalled() {
        FieldDrivenAggregator a = new FieldDrivenAggregator(Map.of("x", "A", "y", "B"),
                Map.of("A", SimCall.ok("A", 10, Map.of("x", 1)), "B", SimCall.ok("B", 99, Map.of("y", 2))));
        var r = a.query(List.of("x"));
        assertThat(r.backendsCalled()).containsExactly("A");
        assertThat(r.latencyMs()).isEqualTo(10);
    }

    @Test
    void slowAndFailingBackendsBecomeMissingNotErrors() {
        var page = new QuoteAggregator(100).aggregate(List.of(
                SimCall.ok("fast", 50, Map.of("premium", 10L)),
                SimCall.ok("slow", 500, Map.of("premium", 5L)),
                SimCall.failing("broken", 10)));
        assertThat(page.quotes()).extracting(QuoteAggregator.Quote::insurer).containsExactly("fast");
        assertThat(page.missing()).hasSize(2);
    }

    @Test
    void criticalPathDeterminesLatency() {
        var finish = DependencyAggregator.schedule(List.of(new Step("a", 10, List.of()), new Step("b", 5, List.of()),
                new Step("c", 7, List.of("a", "b"))));
        assertThat(finish.get("c")).isEqualTo(17);
    }
}
