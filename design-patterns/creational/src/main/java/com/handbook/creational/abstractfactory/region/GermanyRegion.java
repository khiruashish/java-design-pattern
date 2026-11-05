package com.handbook.creational.abstractfactory.region;

import java.util.Locale;

public class GermanyRegion implements RegionFactory {

    @Override
    public TaxPolicy taxPolicy() {
        return new TaxPolicy() {
            public String name() { return "MwSt 7%"; } // reduced VAT rate for food
            public long taxOn(long amount) { return RegionFactory.percentOf(amount, 700); }
        };
    }

    @Override
    public PriceFormatter priceFormatter() {
        return cents -> String.format(Locale.ROOT, "%d,%02d EUR", cents / 100, cents % 100);
    }

    @Override
    public DeliveryRules deliveryRules() {
        return new DeliveryRules() {
            public long minimumOrder() { return 25_00; }
            public long deliveryFee(long subtotal) { return subtotal >= 40_00 ? 0 : 2_99; }
            public String promise() { return "2-hour delivery slot"; }
        };
    }
}
