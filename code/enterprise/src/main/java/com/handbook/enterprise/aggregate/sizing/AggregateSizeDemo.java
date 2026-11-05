package com.handbook.enterprise.aggregate.sizing;

import java.util.ArrayList;
import java.util.List;

/** Coding challenge: a Customer aggregate that contains every order, and the fix. */
public class AggregateSizeDemo {

    static int objectsLoaded;

    record PastOrder(String id, long totalPaise) {
        PastOrder {
            objectsLoaded++;
        }
    }

    /** Too big: placing one order loads (and locks, and re-saves) the whole history. */
    static final class CustomerWithOrders {
        final String id;
        final List<PastOrder> orders = new ArrayList<>();
        CustomerWithOrders(String id, int history) {
            this.id = id;
            objectsLoaded++;
            for (int i = 0; i < history; i++) {
                orders.add(new PastOrder("O" + i, 50_000));
            }
        }
        void placeOrder(String orderId, long paise) { orders.add(new PastOrder(orderId, paise)); }
    }

    /** Right-sized: Order is its own aggregate that references the customer by ID. */
    record CustomerId(String value) { }

    record NewOrder(String id, CustomerId customer, long totalPaise) {
        NewOrder {
            objectsLoaded++;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Finding the Right Aggregate Boundary ===");
        objectsLoaded = 0;
        CustomerWithOrders loyal = new CustomerWithOrders("CUST-1", 5_000); // 5 years of weekly orders
        loyal.placeOrder("O-NEW", 75_000);
        System.out.println("Customer-contains-orders: objects loaded to place 1 order = " + objectsLoaded);

        objectsLoaded = 0;
        NewOrder order = new NewOrder("O-NEW", new CustomerId("CUST-1"), 75_000);
        System.out.println("Order-references-customer-id: objects loaded = " + objectsLoaded + " (" + order.id() + ")");
        System.out.println("Two devices ordering at once no longer conflict on one giant Customer version.");
    }
}
