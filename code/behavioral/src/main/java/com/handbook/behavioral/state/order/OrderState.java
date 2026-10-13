package com.handbook.behavioral.state.order;

/** State interface: every action the order supports. Each state decides what each action means. */
public interface OrderState {

    default OrderState pack(Order o) { return o.reject("pack", this); } // (1)!

    default OrderState dispatch(Order o) { return o.reject("dispatch", this); }

    default OrderState deliver(Order o) { return o.reject("deliver", this); }

    default OrderState cancel(Order o) { return o.reject("cancel", this); }

    String name();
}
