package com.handbook.creational.builder.telecom;

import java.util.List;

/** Representation 1: the product the billing system stores. */
public record MobilePlan(String name, int monthlyPriceInr, int dataGbPerDay,
                         boolean unlimitedCalls, int smsPerDay, List<String> addOns) {

    public MobilePlan {
        addOns = List.copyOf(addOns);
    }
}
