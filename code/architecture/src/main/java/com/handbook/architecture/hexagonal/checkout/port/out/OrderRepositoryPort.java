package com.handbook.architecture.hexagonal.checkout.port.out;

import com.handbook.architecture.hexagonal.checkout.domain.Order;

/** Driven port for persistence. */
public interface OrderRepositoryPort {
    void save(Order order);

    int count();
}
