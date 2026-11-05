package com.handbook.creational.abstractfactory.region;

import java.util.Locale;

/** Challenge solution: a new family, and no change to CheckoutCalculator. */
public class JapanRegion implements RegionFactory {

    @Override
    public TaxPolicy taxPolicy() {
        return new TaxPolicy() {
            public String name() { return "Consumption tax 8%"; } // reduced rate for food
            public long taxOn(long yen) { return yen * 8 / 100; } // yen has no minor unit; round down
        };
    }

    @Override
    public PriceFormatter priceFormatter() {
        return yen -> String.format(Locale.ROOT, "JPY %,d", yen);
    }

    @Override
    public DeliveryRules deliveryRules() {
        return new DeliveryRules() {
            public long minimumOrder() { return 1_000; }
            public long deliveryFee(long subtotal) { return subtotal >= 2_000 ? 0 : 330; }
            public String promise() { return "same-day delivery"; }
        };
    }
}
