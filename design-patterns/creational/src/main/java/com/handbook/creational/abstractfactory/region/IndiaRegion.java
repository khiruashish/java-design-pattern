package com.handbook.creational.abstractfactory.region;

import java.util.Locale;

public class IndiaRegion implements RegionFactory {

    @Override
    public TaxPolicy taxPolicy() {
        return new TaxPolicy() {
            public String name() { return "GST 5%"; }
            public long taxOn(long amount) { return RegionFactory.percentOf(amount, 500); }
        };
    }

    @Override
    public PriceFormatter priceFormatter() {
        return paise -> String.format(Locale.ROOT, "INR %d.%02d", paise / 100, paise % 100);
    }

    @Override
    public DeliveryRules deliveryRules() {
        return new DeliveryRules() {
            public long minimumOrder() { return 99_00; }
            public long deliveryFee(long subtotal) { return subtotal >= 199_00 ? 0 : 25_00; }
            public String promise() { return "10-minute delivery"; }
        };
    }
}
