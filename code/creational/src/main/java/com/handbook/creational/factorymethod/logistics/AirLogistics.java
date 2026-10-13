package com.handbook.creational.factorymethod.logistics;

public class AirLogistics extends LogisticsPlanner {

    @Override
    protected Transport createTransport() {
        return new CargoPlane();
    }
}
