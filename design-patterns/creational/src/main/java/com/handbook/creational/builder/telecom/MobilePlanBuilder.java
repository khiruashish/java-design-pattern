package com.handbook.creational.builder.telecom;

import java.util.ArrayList;
import java.util.List;

public class MobilePlanBuilder implements PlanBuilder {

    private String name;
    private int price;
    private int data;
    private boolean calls;
    private int sms;
    private final List<String> addOns = new ArrayList<>();

    public void name(String name) { this.name = name; }
    public void monthlyPriceInr(int price) { this.price = price; }
    public void data(int gbPerDay) { this.data = gbPerDay; }
    public void unlimitedCalls() { this.calls = true; }
    public void sms(int perDay) { this.sms = perDay; }
    public void addOn(String addOn) { addOns.add(addOn); }

    public MobilePlan result() {
        return new MobilePlan(name, price, data, calls, sms, addOns);
    }
}
