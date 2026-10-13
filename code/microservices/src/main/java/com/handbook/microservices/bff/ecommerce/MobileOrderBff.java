package com.handbook.microservices.bff.ecommerce;

import com.handbook.microservices.bff.ecommerce.OrderBackends.OrderService;
import com.handbook.microservices.bff.ecommerce.OrderBackends.ShippingService;
import com.handbook.microservices.support.Json;

import java.util.Map;

/** Mobile order card: a one-line summary and live status. No review service call at all. */
public final class MobileOrderBff {

    private final OrderService orders;
    private final ShippingService shipping;

    public MobileOrderBff(OrderService orders, ShippingService shipping) {
        this.orders = orders;
        this.shipping = shipping;
    }

    public Map<String, Object> orderCard(String orderId) {
        var order = orders.get(orderId);
        var shipment = shipping.forOrder(orderId);
        int items = order.lines().stream().mapToInt(l -> l.qty()).sum();
        return Json.obj(
                "order", order.id(),
                "summary", items + " items · " + WebBff.rupees(order.lines().stream()
                        .mapToLong(l -> l.qty() * l.pricePaise()).sum()), // (1)!
                "status", "Arriving by " + shipment.eta()); // (2)!
    }
}
