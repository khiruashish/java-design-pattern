package com.handbook.microservices.dbperservice.grocery;

public class DatabasePerServiceDemo {

    public static void main(String[] args) {
        var orders = new OrderService();
        var customers = new CustomerService();

        System.out.println("=== Grocery: orders_db and customers_db are separate databases ===");
        try {
            orders.recentOrdersWithJoin();
        } catch (IllegalStateException e) {
            System.out.println("SQL JOIN across services fails: " + e.getMessage());
        }

        System.out.println("API composition instead:");
        new OrderHistoryComposer(orders, customers).history().forEach(line -> System.out.println("  " + line));
        System.out.println("customer-service API calls: " + customers.apiCalls() + " (one per order, see the coding challenge)");
    }
}
