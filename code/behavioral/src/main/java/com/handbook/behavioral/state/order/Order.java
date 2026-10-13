package com.handbook.behavioral.state.order;

/** Context: delegates every action to its current state object. */
public class Order {

    private final String id;
    private OrderState state = new States.Placed();

    public Order(String id) {
        this.id = id;
    }

    public void pack() { state = state.pack(this); }         // (1)!

    public void dispatch() { state = state.dispatch(this); }

    public void deliver() { state = state.deliver(this); }

    public void cancel() { state = state.cancel(this); }

    public String status() { return state.name(); }

    void log(String message) {
        System.out.println("  " + id + " [" + state.name() + "] " + message);
    }

    OrderState reject(String action, OrderState current) {
        System.out.println("  " + id + " [" + current.name() + "] cannot " + action);
        return current; // stay in the same state
    }
}
