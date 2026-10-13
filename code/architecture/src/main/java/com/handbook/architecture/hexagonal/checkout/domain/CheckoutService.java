package com.handbook.architecture.hexagonal.checkout.domain;

import com.handbook.architecture.hexagonal.checkout.port.in.PlaceOrderUseCase;
import com.handbook.architecture.hexagonal.checkout.port.out.NotificationPort;
import com.handbook.architecture.hexagonal.checkout.port.out.OrderRepositoryPort;
import com.handbook.architecture.hexagonal.checkout.port.out.PaymentPort;

/**
 * The hexagon's core: implements the driving port, depends only on driven PORTS (interfaces it
 * owns), never on adapters. It can run in a unit test, a web app or a CLI unchanged.
 */
public class CheckoutService implements PlaceOrderUseCase {

    private final PaymentPort payments;
    private final OrderRepositoryPort orders;
    private final NotificationPort notifications;
    private int nextId = 1;

    public CheckoutService(PaymentPort payments, OrderRepositoryPort orders, NotificationPort notifications) {
        this.payments = payments; // (1)!
        this.orders = orders;
        this.notifications = notifications;
    }

    @Override
    public Order placeOrder(PlaceOrderCommand cmd) {
        String id = "ORD-" + nextId++;
        boolean paid = payments.charge(cmd.customerId(), cmd.totalPaise(), id);
        Order order = new Order(id, cmd.customerId(), cmd.totalPaise(),
                paid ? Order.Status.PAID : Order.Status.PAYMENT_DECLINED);
        orders.save(order);
        notifications.notify(cmd.phone(), paid ? "Order " + id + " confirmed" : "Payment for " + id + " declined");
        return order;
    }
}
