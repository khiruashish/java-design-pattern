package com.handbook.enterprise.domainevents.grocery;

import com.handbook.enterprise.domainevents.grocery.DomainEvents.AggregateRoot;
import com.handbook.enterprise.domainevents.grocery.DomainEvents.OrderCancelled;
import com.handbook.enterprise.domainevents.grocery.DomainEvents.OrderPlaced;

/** The aggregate states WHAT happened; it doesn't know who cares (loyalty, SMS, stock). */
public final class Order extends AggregateRoot {

    private final String id;
    private final String customerId;
    private final long totalPaise;
    private String status = "NEW";

    public Order(String id, String customerId, long totalPaise) {
        this.id = id;
        this.customerId = customerId;
        this.totalPaise = totalPaise;
    }

    public void place() {
        status = "PLACED";
        record(new OrderPlaced(id, customerId, totalPaise));
    }

    public void cancel(String reason) {
        if (!status.equals("PLACED")) {
            throw new IllegalStateException("only placed orders can be cancelled");
        }
        status = "CANCELLED";
        record(new OrderCancelled(id, reason));
    }
}
