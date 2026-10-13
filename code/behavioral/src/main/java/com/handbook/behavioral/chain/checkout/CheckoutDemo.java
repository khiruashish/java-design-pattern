package com.handbook.behavioral.chain.checkout;

import com.handbook.behavioral.chain.checkout.Validators.*;

import java.util.List;

public class CheckoutDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Checkout Validation Chain ===");
        CheckoutValidator chain = new ServiceableArea();
        chain.then(new StockAvailable()).then(new CouponValid()).then(new CodLimit());

        List<Order> orders = List.of(
                new Order("O-1", "560034", "MILK-1L", 2, null, 136),
                new Order("O-2", "110001", "MILK-1L", 2, null, 136),
                new Order("O-3", "560038", "MANGO-1KG", 5, null, 900),
                new Order("O-4", "560102", "MILK-1L", 3, "FRESH50", 204),
                new Order("O-5", "560034", "MILK-1L", 30, null, 6_000));

        for (Order o : orders) {
            System.out.println(o.id() + ": " + chain.validate(o).map(p -> "REJECTED - " + p).orElse("OK, proceed to payment"));
        }
    }
}
