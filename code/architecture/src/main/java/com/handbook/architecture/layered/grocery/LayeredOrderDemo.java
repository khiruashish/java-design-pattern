package com.handbook.architecture.layered.grocery;

import com.handbook.architecture.layered.grocery.repository.OrderRepository;
import com.handbook.architecture.layered.grocery.service.OrderService;
import com.handbook.architecture.layered.grocery.web.OrderController;

import java.util.LinkedHashMap;
import java.util.Map;

public class LayeredOrderDemo {

    public static void main(String[] args) {
        OrderRepository repository = new OrderRepository();
        OrderController controller = new OrderController(new OrderService(repository)); // wiring = Spring's job

        System.out.println("=== Grocery: one request through three layers ===");
        System.out.println("-> " + controller.post(form("customer", "CUST-42", "MANGO-1KG", "2", "RICE-5KG", "1")));
        System.out.println("-> " + controller.post(form("customer", "CUST-42", "MANGO-1KG", "two")));
        System.out.println("-> " + controller.post(form("customer", "CUST-42", "SAFFRON-1G", "1")));
        System.out.println("-> " + controller.post(form("customer", "CUST-42", "RICE-5KG", "25")));
        System.out.println("Orders stored: " + repository.count());
    }

    private static Map<String, String> form(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put(kv[i], kv[i + 1]);
        }
        return m;
    }
}
