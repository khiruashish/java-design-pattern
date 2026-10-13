package com.handbook.architecture.hexagonal.checkout.port.in;

import com.handbook.architecture.hexagonal.checkout.domain.Order;

/** Driving (primary) port: what the application offers to the outside world. */
public interface PlaceOrderUseCase {

    record PlaceOrderCommand(String customerId, String phone, long totalPaise) {
        public PlaceOrderCommand {
            if (totalPaise <= 0) {
                throw new IllegalArgumentException("total must be positive");
            }
        }
    }

    Order placeOrder(PlaceOrderCommand command);
}
