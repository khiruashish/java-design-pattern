package com.handbook.principles.oop.inheritance;

public class Truck extends Vehicle {
    private final int axles;

    public Truck(String plate, int axles) {
        super(plate, axles * 2 + 2);
        this.axles = axles;
    }

    @Override
    protected long tollCents() { return 400L * axles; }   // specialised rule
}
