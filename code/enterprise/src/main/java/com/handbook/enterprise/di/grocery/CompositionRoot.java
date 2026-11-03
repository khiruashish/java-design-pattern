package com.handbook.enterprise.di.grocery;

import com.handbook.enterprise.di.grocery.Checkout.CheckoutService;
import com.handbook.enterprise.di.grocery.Checkout.DarkStoreSlots;
import com.handbook.enterprise.di.grocery.Checkout.FakeGateway;
import com.handbook.enterprise.di.grocery.Checkout.Notifier;
import com.handbook.enterprise.di.grocery.Checkout.RazorPayGateway;
import com.handbook.enterprise.di.grocery.Checkout.SmsNotifier;

/** The ONE place that knows concrete classes ("pure DI": no framework, just constructors). */
public final class CompositionRoot {

    public static CheckoutService production() { // (1)!
        return new CheckoutService(new RazorPayGateway(), new DarkStoreSlots(), new SmsNotifier());
    }

    public static CheckoutService demo(Notifier notifier) { // (2)!
        return new CheckoutService(new FakeGateway(), new DarkStoreSlots(), notifier);
    }

    private CompositionRoot() {
    }
}
