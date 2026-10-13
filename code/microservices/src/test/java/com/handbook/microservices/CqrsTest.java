package com.handbook.microservices;

import com.handbook.microservices.cqrs.catalog.CatalogCommands;
import com.handbook.microservices.cqrs.catalog.CatalogSearchView;
import com.handbook.microservices.cqrs.rebuild.TopSellersProjection;
import com.handbook.microservices.cqrs.rebuild.TopSellersProjection.OrderPlaced;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CqrsTest {

    @Test
    void readModelIsEventuallyConsistent() {
        var view = new CatalogSearchView();
        var commands = new CatalogCommands(view::enqueue);
        commands.addProduct("A", "Apple", "fruit", 100);
        assertThat(view.cheapest("fruit", 5)).isEmpty();
        view.catchUp();
        assertThat(view.cheapest("fruit", 5)).containsExactly("Apple ₹1");
    }

    @Test
    void projectionIgnoresRedeliveredEvents() {
        var p = new TopSellersProjection();
        p.apply(new OrderPlaced(1, "X", 2));
        p.apply(new OrderPlaced(1, "X", 2));
        assertThat(p.top(1)).containsExactly("X=2");
    }
}
