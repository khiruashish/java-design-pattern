package com.handbook.enterprise.domainevents;

import com.handbook.enterprise.domainevents.grocery.DomainEvents.Dispatcher;
import com.handbook.enterprise.domainevents.grocery.DomainEvents.OrderPlaced;
import com.handbook.enterprise.domainevents.grocery.Order;
import com.handbook.enterprise.domainevents.healthcare.LabEvents;
import com.handbook.enterprise.domainevents.healthcare.LabEvents.LabOrder;
import com.handbook.enterprise.domainevents.healthcare.LabEvents.ResultRecorded;
import com.handbook.enterprise.domainevents.insurance.PolicyEvents;
import com.handbook.enterprise.domainevents.insurance.PolicyEvents.PolicyLapsed;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventsTest {

    @Test
    void aggregateRecordsEventsAndPullClearsThem() {
        Order o = new Order("O", "C", 100);
        o.place();
        assertThat(o.pullEvents()).containsExactly(new OrderPlaced("O", "C", 100));
        assertThat(o.pullEvents()).isEmpty();
    }

    @Test
    void dispatcherRoutesByType() {
        List<String> seen = new ArrayList<>();
        Order o = new Order("O", "C", 100);
        o.place();
        o.cancel("x");
        new Dispatcher().on(OrderPlaced.class, e -> seen.add(e.orderId())).dispatch(o.pullEvents());
        assertThat(seen).containsExactly("O");
    }

    @Test
    void criticalResultsPageTheDoctor() {
        LabOrder lab = new LabOrder("P", Clock.systemUTC());
        lab.recordPotassium(2.0);
        var event = lab.pullEvents().get(0);
        assertThat(event).isInstanceOf(ResultRecorded.class);
        assertThat(LabEvents.describe(event)).startsWith("PAGE");
    }

    @Test
    void integrationEventDropsInternalFields() {
        var v1 = PolicyEvents.toIntegrationEvent(new PolicyLapsed("P", "C", LocalDate.EPOCH, 3, "secret", 0.9));
        assertThat(v1.toString()).doesNotContain("secret").doesNotContain("0.9");
        assertThat(v1.schemaVersion()).isEqualTo(1);
    }
}
