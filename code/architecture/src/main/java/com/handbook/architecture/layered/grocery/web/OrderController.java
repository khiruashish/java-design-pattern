package com.handbook.architecture.layered.grocery.web;

import com.handbook.architecture.layered.grocery.domain.Order;
import com.handbook.architecture.layered.grocery.service.OrderService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Presentation layer: translates HTTP input into a service call and the result into a response.
 * No business rules here, and no direct access to the repository.
 */
public class OrderController {

    /** The response DTO: what the API exposes, decoupled from the domain object. */
    public record OrderResponse(int status, String body) {
    }

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /** POST /orders with form fields like {@code customer=CUST-42&MANGO-1KG=2}. */
    public OrderResponse post(Map<String, String> form) {
        System.out.println("    [web] POST /orders " + form);
        Map<String, Integer> quantities = new LinkedHashMap<>();
        try {
            form.forEach((k, v) -> {
                if (!k.equals("customer")) {
                    quantities.put(k, Integer.parseInt(v)); // (1)!
                }
            });
        } catch (NumberFormatException e) {
            return new OrderResponse(400, "quantities must be numbers");
        }
        try {
            Order order = service.placeOrder(form.get("customer"), quantities);
            return new OrderResponse(201, order.id() + " total ₹" + order.totalPaise() / 100); // (2)!
        } catch (OrderService.InvalidOrderException e) {
            return new OrderResponse(422, e.getMessage()); // (3)!
        }
    }
}
