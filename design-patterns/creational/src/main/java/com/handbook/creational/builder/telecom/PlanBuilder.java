package com.handbook.creational.builder.telecom;

/**
 * GoF Builder interface. Different builders turn the same construction steps
 * into different representations.
 */
public interface PlanBuilder {

    void name(String name);

    void monthlyPriceInr(int price);

    void data(int gbPerDay);

    void unlimitedCalls();

    void sms(int perDay);

    void addOn(String addOn);
}
