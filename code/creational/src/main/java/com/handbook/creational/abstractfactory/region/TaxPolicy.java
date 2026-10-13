package com.handbook.creational.abstractfactory.region;

public interface TaxPolicy {

    String name();

    /** Tax on an amount in minor units (paise, cents, yen), rounded half-up. */
    long taxOn(long amountMinor);
}
