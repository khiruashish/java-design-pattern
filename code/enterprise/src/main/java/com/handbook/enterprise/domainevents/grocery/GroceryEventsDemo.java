package com.handbook.enterprise.domainevents.grocery;

import com.handbook.enterprise.domainevents.grocery.DomainEvents.Dispatcher;
import com.handbook.enterprise.domainevents.grocery.DomainEvents.OrderCancelled;
import com.handbook.enterprise.domainevents.grocery.DomainEvents.OrderPlaced;

public class GroceryEventsDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Order Raises Events, Other Modules React ===");
        Dispatcher dispatcher = new Dispatcher()
                .on(OrderPlaced.class, e -> System.out.println("  [loyalty]   +" + e.totalPaise() / 10_000 + " points for " + e.customerId()))
                .on(OrderPlaced.class, e -> System.out.println("  [sms]       'Order " + e.orderId() + " confirmed' -> " + e.customerId()))
                .on(OrderPlaced.class, e -> System.out.println("  [warehouse] start picking " + e.orderId()))
                .on(OrderCancelled.class, e -> System.out.println("  [warehouse] stop picking " + e.orderId() + " (" + e.reason() + ")"));

        Order order = new Order("ORD-31", "CUST-5", 1_245_000);
        order.place();
        System.out.println("Use case: order.place() then save in a transaction... committed. Dispatching:");
        dispatcher.dispatch(order.pullEvents()); // (1)!

        order.cancel("customer changed mind");
        System.out.println("Use case: order.cancel() committed. Dispatching:");
        dispatcher.dispatch(order.pullEvents());
        System.out.println("Events left after dispatch: " + order.pullEvents().size());
    }
}
