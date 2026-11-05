package com.handbook.behavioral.state.order;

/** Concrete states. Each overrides only the actions that are legal in it. */
public final class States {

    private States() {
    }

    public static final class Placed implements OrderState {
        public OrderState pack(Order o) { o.log("picker packed 6 items"); return new Packed(); }
        public OrderState cancel(Order o) { o.log("full refund issued"); return new Cancelled(); }
        public String name() { return "PLACED"; }
    }

    public static final class Packed implements OrderState {
        public OrderState dispatch(Order o) { o.log("rider assigned"); return new OutForDelivery(); }
        public OrderState cancel(Order o) { o.log("items restocked, full refund"); return new Cancelled(); }
        public String name() { return "PACKED"; }
    }

    public static final class OutForDelivery implements OrderState {
        public OrderState deliver(Order o) { o.log("OTP verified at door"); return new Delivered(); }
        public String name() { return "OUT_FOR_DELIVERY"; } // cancel not allowed: rider is on the way
    }

    public static final class Delivered implements OrderState {
        public String name() { return "DELIVERED"; }
    }

    public static final class Cancelled implements OrderState {
        public String name() { return "CANCELLED"; }
    }
}
