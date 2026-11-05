package com.handbook.creational.abstractfactory.region;

public interface DeliveryRules {

    long minimumOrder();

    long deliveryFee(long subtotalMinor);

    String promise();
}
