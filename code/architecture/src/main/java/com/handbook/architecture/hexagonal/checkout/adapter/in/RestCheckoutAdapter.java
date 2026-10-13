package com.handbook.architecture.hexagonal.checkout.adapter.in;

import com.handbook.architecture.hexagonal.checkout.domain.Order;
import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase;
import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase.PlaceOrderCommand;

import java.util.Map;

/** Driving adapter: HTTP/JSON → command → use case → HTTP response. (A @RestController in Spring.) */
public class RestCheckoutAdapter {

    private final PlaceOrderUseCase useCase;

    public RestCheckoutAdapter(PlaceOrderUseCase useCase) {
        this.useCase = useCase;
    }

    public String post(Map<String, String> json) {
        try {
            Order o = useCase.placeOrder(new PlaceOrderCommand(json.get("customerId"), json.get("phone"),
                    Math.round(Double.parseDouble(json.get("totalRupees")) * 100)));
            return (o.status() == Order.Status.PAID ? "201 " : "402 ") + o;
        } catch (IllegalArgumentException e) {
            return "400 " + e.getMessage();
        }
    }
}
