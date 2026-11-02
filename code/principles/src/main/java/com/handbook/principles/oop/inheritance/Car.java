package com.handbook.principles.oop.inheritance;

public class Car extends Vehicle {
    public Car(String plate) { super(plate, 4); }

    @Override
    protected long tollCents() { return 250; }
}
