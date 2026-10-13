package com.handbook.microservices.dbperservice.batching;

import com.handbook.microservices.dbperservice.grocery.CustomerService;
import com.handbook.microservices.dbperservice.grocery.OrderService;

public class BatchedComposerDemo {

    public static void main(String[] args) {
        var customers = new CustomerService();
        System.out.println("=== Challenge: API composition without N+1 calls ===");
        new BatchedComposer(new OrderService(), customers).history().forEach(line -> System.out.println("  " + line));
        System.out.println("customer-service API calls: " + customers.apiCalls());
    }
}
