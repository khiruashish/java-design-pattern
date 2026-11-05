package com.handbook.creational.factorymethod.logistics;

public record FreightTrain() implements Transport {
    public String name() { return "Train"; }
    public int costPerKmInr() { return 8; }
    public int speedKmph() { return 60; }
}
