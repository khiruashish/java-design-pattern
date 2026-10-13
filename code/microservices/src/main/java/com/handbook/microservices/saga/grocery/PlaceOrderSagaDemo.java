package com.handbook.microservices.saga.grocery;

import com.handbook.microservices.saga.grocery.GroceryServices.DeliveryService;
import com.handbook.microservices.saga.grocery.GroceryServices.InventoryService;
import com.handbook.microservices.saga.grocery.GroceryServices.PaymentService;
import com.handbook.microservices.saga.grocery.PlaceOrderSaga.Order;

public class PlaceOrderSagaDemo {

    public static void main(String[] args) {
        var inventory = new InventoryService();
        var payments = new PaymentService();
        var saga = new PlaceOrderSaga(inventory, payments, new DeliveryService());

        System.out.println("=== Grocery: place-order saga (orchestration) ===");
        System.out.println("Outcome: " + saga.place(new Order("ORD-1", "MANGO-1KG", 2, 79_800, "560034")));
        System.out.println("Stock left: " + inventory.available("MANGO-1KG") + ", ORD-1 charged: " + payments.isCharged("ORD-1"));

        System.out.println();
        System.out.println("Outcome: " + saga.place(new Order("ORD-2", "MANGO-1KG", 3, 119_700, "999999")));
        System.out.println("Stock left: " + inventory.available("MANGO-1KG") + ", ORD-2 charged: " + payments.isCharged("ORD-2"));

        System.out.println();
        System.out.println("Outcome: " + saga.place(new Order("ORD-3", "MANGO-1KG", 50, 1_995_000, "560001")));
    }
}
