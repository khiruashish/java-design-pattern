package com.handbook.creational.abstractfactory.region;

/** Abstract factory for everything that varies by country. */
public interface RegionFactory {

    TaxPolicy taxPolicy();

    PriceFormatter priceFormatter();

    DeliveryRules deliveryRules();

    /** Helper shared by tax policies: basis points, rounded half-up. */
    static long percentOf(long amountMinor, int basisPoints) {
        return (amountMinor * basisPoints + 5_000) / 10_000;
    }
}
