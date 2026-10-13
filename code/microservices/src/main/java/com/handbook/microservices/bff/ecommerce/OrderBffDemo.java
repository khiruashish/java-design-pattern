package com.handbook.microservices.bff.ecommerce;

import com.handbook.microservices.bff.ecommerce.OrderBackends.OrderService;
import com.handbook.microservices.bff.ecommerce.OrderBackends.ReviewService;
import com.handbook.microservices.bff.ecommerce.OrderBackends.ShippingService;
import com.handbook.microservices.support.Json;

public class OrderBffDemo {

    public static void main(String[] args) {
        var orders = new OrderService();
        var shipping = new ShippingService();

        System.out.println("=== E-commerce: order details for web vs. mobile ===");
        String web = Json.of(new WebBff(orders, shipping, new ReviewService()).orderPage("ORD-9"));
        String mobile = Json.of(new MobileOrderBff(orders, shipping).orderCard("ORD-9"));
        System.out.println("Web BFF    (3 backend calls) -> " + web);
        System.out.println("Mobile BFF (2 backend calls) -> " + mobile);
        System.out.printf("Mobile payload is %d%% of the web payload%n", mobile.length() * 100 / web.length());
    }
}
