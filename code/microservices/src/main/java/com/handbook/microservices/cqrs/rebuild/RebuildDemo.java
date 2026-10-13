package com.handbook.microservices.cqrs.rebuild;

import com.handbook.microservices.cqrs.rebuild.TopSellersProjection.OrderPlaced;

import java.util.List;

public class RebuildDemo {

    public static void main(String[] args) {
        List<OrderPlaced> history = List.of(
                new OrderPlaced(1, "MANGO-1KG", 2), new OrderPlaced(2, "RICE-5KG", 1),
                new OrderPlaced(3, "MANGO-1KG", 3), new OrderPlaced(4, "MILK-1L", 6),
                new OrderPlaced(5, "RICE-5KG", 1), new OrderPlaced(6, "MILK-1L", 2));

        System.out.println("=== Challenge: add a new read model by replaying history ===");
        var topSellers = new TopSellersProjection();
        history.forEach(topSellers::apply);
        System.out.println("Rebuilt from " + history.size() + " stored events -> top 2: " + topSellers.top(2)
                + ", checkpoint=" + topSellers.checkpoint());

        System.out.println("Live events continue; event 6 is redelivered by the broker:");
        topSellers.apply(new OrderPlaced(6, "MILK-1L", 2));
        topSellers.apply(new OrderPlaced(7, "MANGO-1KG", 4));
        System.out.println("top 2: " + topSellers.top(2) + ", checkpoint=" + topSellers.checkpoint());
    }
}
