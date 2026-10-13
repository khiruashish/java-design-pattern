package com.handbook.microservices.bff.ecommerce;

import com.handbook.microservices.bff.ecommerce.OrderBackends.Line;
import com.handbook.microservices.bff.ecommerce.OrderBackends.OrderService;
import com.handbook.microservices.bff.ecommerce.OrderBackends.ReviewService;
import com.handbook.microservices.bff.ecommerce.OrderBackends.ShippingService;
import com.handbook.microservices.support.Json;

import java.util.Map;

/** Desktop order page: everything on one wide screen, including the tracking timeline and review prompts. */
public final class WebBff {

    private final OrderService orders;
    private final ShippingService shipping;
    private final ReviewService reviews;

    public WebBff(OrderService orders, ShippingService shipping, ReviewService reviews) {
        this.orders = orders;
        this.shipping = shipping;
        this.reviews = reviews;
    }

    public Map<String, Object> orderPage(String orderId) {
        var order = orders.get(orderId);
        var shipment = shipping.forOrder(orderId);
        var prompts = reviews.prompts(order.customerId(), order.lines().stream().map(Line::sku).toList()); // (1)!
        return Json.obj(
                "order", order.id(),
                "items", order.lines().stream()
                        .map(l -> Json.obj("name", l.name(), "qty", l.qty(), "price", rupees(l.pricePaise())))
                        .toList(),
                "total", rupees(order.lines().stream().mapToLong(l -> l.qty() * l.pricePaise()).sum()),
                "tracking", Json.obj("carrier", shipment.carrier(), "status", shipment.status(),
                        "timeline", shipment.scans()),
                "reviewSkus", prompts.stream().filter(p -> p.eligible()).map(p -> p.sku()).toList());
    }

    static String rupees(long paise) { // (2)!
        return String.format("₹%,d.%02d", paise / 100, paise % 100);
    }
}
