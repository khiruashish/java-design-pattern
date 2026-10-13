package com.handbook.behavioral.state.order;

public class OrderDemo {

    public static void main(String[] args) {
        System.out.println("=== Grocery: Order Lifecycle ===");
        Order happy = new Order("GRO-1");
        happy.pack();
        happy.dispatch();
        happy.cancel();   // too late
        happy.deliver();
        happy.deliver();  // already delivered
        System.out.println("GRO-1 final status: " + happy.status());

        Order changedMind = new Order("GRO-2");
        changedMind.pack();
        changedMind.cancel();
        changedMind.dispatch(); // cancelled orders go nowhere
        System.out.println("GRO-2 final status: " + changedMind.status());
    }
}
