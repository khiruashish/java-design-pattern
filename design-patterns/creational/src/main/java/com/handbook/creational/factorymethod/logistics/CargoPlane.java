package com.handbook.creational.factorymethod.logistics;

public record CargoPlane() implements Transport {
    public String name() { return "Plane"; }
    public int costPerKmInr() { return 95; }
    public int speedKmph() { return 700; }
}
