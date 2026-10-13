package com.handbook.microservices;

import com.handbook.microservices.decomposition.grocery.GroceryServices.*;
import com.handbook.microservices.decomposition.healthcare.BoundaryChecker;
import com.handbook.microservices.decomposition.ridehailing.TripContexts;
import com.handbook.microservices.support.EventBus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DecompositionTest {

    @Test
    void servicesCollaborateOnlyThroughApisAndEvents() {
        EventBus bus = new EventBus(false);
        CatalogService catalog = new CatalogService(bus);
        OrderService orders = new OrderService(new InventoryService(bus), bus);
        assertThat(orders.place("MANGO-1KG", 3)).startsWith("accepted");
        assertThat(catalog.describe("MANGO-1KG")).endsWith("(out of stock)");
        assertThat(orders.place("MANGO-1KG", 1)).startsWith("rejected");
    }

    @Test
    void billingTranslatesDispatchEventIntoItsOwnModel() {
        var dispatch = new TripContexts.Dispatch();
        var billing = new TripContexts.Billing();
        dispatch.onTripCompleted(billing::handle);
        dispatch.complete(new TripContexts.Dispatch.Trip("T", "D", 0, 0, 1, "X"), "R", 10, 10);
        assertThat(billing.invoiced()).singleElement().extracting(TripContexts.Billing.Trip::totalInr).isEqualTo(50 + 140 + 20 + 20);
    }

    @Test
    void crossServiceWritesAreFlagged() {
        var report = BoundaryChecker.check(Map.of("a", "S1", "b", "S2"),
                List.of(new BoundaryChecker.UseCase("uc", Set.of("a", "b"), Set.of())));
        assertThat(report.get("uc")).startsWith("WARN");
    }
}
