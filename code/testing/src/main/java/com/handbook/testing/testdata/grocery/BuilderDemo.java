package com.handbook.testing.testdata.grocery;

import static com.handbook.testing.testdata.grocery.OrderBuilder.anOrder;

public class BuilderDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Delivery-Fee Tests That Say Only What Matters ===");
        print("small order           ", anOrder().withSubtotal(30_000).build());
        print("order above INR 499   ", anOrder().withSubtotal(50_000).build());
        print("small + express       ", anOrder().withSubtotal(30_000).express().build());
        print("first order + express ", anOrder().withSubtotal(30_000).express().firstOrder().build());
        System.out.println("Each case sets 1-3 fields; the other 6 fields of Order come from defaults.");
    }

    private static void print(String label, Order o) {
        System.out.println(label + "-> fee INR " + o.deliveryFeePaise() / 100);
    }
}
