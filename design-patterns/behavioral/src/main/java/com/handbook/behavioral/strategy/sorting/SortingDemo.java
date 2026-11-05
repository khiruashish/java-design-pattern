package com.handbook.behavioral.strategy.sorting;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Challenge solution: sort options on a product listing page as Comparator strategies. */
public class SortingDemo {

    record Product(String name, int priceInr, double rating, int reviews, double relevance) {
    }

    static final Map<String, Comparator<Product>> SORTS = Map.of(
            "price_asc", Comparator.comparingInt(Product::priceInr),
            "rating", Comparator.comparingDouble(Product::rating).reversed()
                    .thenComparing(Comparator.comparingInt(Product::reviews).reversed()), // tie-breaker
            "relevance", Comparator.comparingDouble(Product::relevance).reversed()
                    .thenComparingInt(Product::priceInr));

    public static void main(String[] args) {
        List<Product> results = List.of(
                new Product("Steel Bottle 1L", 499, 4.5, 1200, 0.92),
                new Product("Copper Bottle 1L", 899, 4.7, 310, 0.88),
                new Product("Glass Bottle 750ml", 349, 4.5, 4100, 0.92),
                new Product("Plastic Bottle 1L", 149, 3.9, 800, 0.60));

        for (String sort : List.of("relevance", "price_asc", "rating")) {
            List<String> names = results.stream().sorted(SORTS.get(sort)).map(Product::name).toList();
            System.out.printf("%-10s %s%n", sort, names);
        }
    }
}
