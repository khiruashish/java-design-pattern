package com.handbook.creational.factorymethod.logistics;

public record Truck() implements Transport {
    public String name() { return "Truck"; }
    public int costPerKmInr() { return 18; }
    public int speedKmph() { return 50; }
}
