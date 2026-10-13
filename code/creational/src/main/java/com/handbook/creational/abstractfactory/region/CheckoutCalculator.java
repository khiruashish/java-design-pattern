package com.handbook.creational.abstractfactory.region;

/** Client: no country-specific code anywhere in this class. */
public class CheckoutCalculator {

    private final TaxPolicy tax;
    private final PriceFormatter fmt;
    private final DeliveryRules delivery;

    public CheckoutCalculator(RegionFactory region) {
        this.tax = region.taxPolicy(); // (1)!
        this.fmt = region.priceFormatter();
        this.delivery = region.deliveryRules();
    }

    public String summary(long subtotalMinor) {
        if (subtotalMinor < delivery.minimumOrder()) {
            return "Minimum order is " + fmt.format(delivery.minimumOrder());
        }
        long taxAmount = tax.taxOn(subtotalMinor);
        long fee = delivery.deliveryFee(subtotalMinor);
        long total = subtotalMinor + taxAmount + fee;
        return "subtotal %s + %s %s + delivery %s = %s (%s)".formatted(
                fmt.format(subtotalMinor), tax.name(), fmt.format(taxAmount),
                fmt.format(fee), fmt.format(total), delivery.promise());
    }
}
