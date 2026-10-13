package com.handbook.creational.factorymethod.logistics;

public class RoadLogistics extends LogisticsPlanner {

    @Override
    protected Transport createTransport() {
        return new Truck();
    }
}
