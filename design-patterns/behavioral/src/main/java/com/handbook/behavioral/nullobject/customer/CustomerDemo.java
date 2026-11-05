package com.handbook.behavioral.nullobject.customer;

import java.util.Map;
import java.util.Optional;

/** Challenge solution: Optional at the repository boundary, Null Object inside the domain. */
public class CustomerDemo {

    interface Customer {
        String name();
        String tier();
        int loyaltyDiscountPercent();
    }

    record RegisteredCustomer(String name, String tier) implements Customer {
        public int loyaltyDiscountPercent() {
            return switch (tier) {
                case "GOLD" -> 5;
                case "PLATINUM" -> 10;
                default -> 0;
            };
        }
    }

    static final Customer UNKNOWN = new Customer() {
        public String name() { return "guest"; }
        public String tier() { return "NONE"; }
        public int loyaltyDiscountPercent() { return 0; }
    };

    static final Map<String, Customer> DB = Map.of("C1", new RegisteredCustomer("Asha", "PLATINUM"),
            "C2", new RegisteredCustomer("Ravi", "SILVER"));

    static Optional<Customer> findById(String id) { // repository: absence is explicit
        return Optional.ofNullable(DB.get(id));
    }

    static int priceFor(String customerId, int basePrice) {
        Customer c = findById(customerId).orElse(UNKNOWN); // boundary: convert absence to a Null Object
        return basePrice - basePrice * c.loyaltyDiscountPercent() / 100;
    }

    public static void main(String[] args) {
        for (String id : new String[] {"C1", "C2", "C999"}) {
            Customer c = findById(id).orElse(UNKNOWN);
            System.out.printf("%-5s %-6s tier=%-8s price of INR 1000 item: INR %d%n", id, c.name(), c.tier(), priceFor(id, 1000));
        }
    }
}
