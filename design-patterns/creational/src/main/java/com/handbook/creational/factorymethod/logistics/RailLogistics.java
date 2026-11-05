package com.handbook.creational.factorymethod.logistics;

public class RailLogistics extends LogisticsPlanner {

    @Override
    protected Transport createTransport() {
        return new FreightTrain();
    }
}
