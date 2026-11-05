package com.handbook.enterprise.repository.grocery;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GroceryRepositoryDemo {

    public static void main(String[] args) throws SQLException {
        System.out.println("=== Grocery: One Restock Service, Two Repository Adapters ===");
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:catalog;DB_CLOSE_DELAY=-1");
        try (Connection c = ds.getConnection()) {
            c.createStatement().execute("CREATE TABLE product (sku VARCHAR PRIMARY KEY, name VARCHAR, category VARCHAR,"
                    + " price_paise BIGINT, stock INT)");
        }
        for (ProductRepository repo : List.of(new InMemoryProductRepository(), new JdbcProductRepository(ds))) {
            repo.save(new Product("MILK-1L", "Toned Milk 1L", "dairy", 6_800, 4));
            repo.save(new Product("ATTA-5KG", "Wheat Atta 5kg", "staples", 28_500, 40));
            repo.save(new Product("EGG-12", "Eggs (12)", "dairy", 9_000, 2));
            List<String> restocked = new RestockService(repo).restockLowItems(10, 50);
            System.out.printf("%-26s restocked %s, MILK-1L stock now %d%n", repo.getClass().getSimpleName(), restocked,
                    repo.findBySku("MILK-1L").orElseThrow().stock());
        }
        System.out.println("RestockService has no SQL and no Map: storage is a detail behind the port.");
    }
}
