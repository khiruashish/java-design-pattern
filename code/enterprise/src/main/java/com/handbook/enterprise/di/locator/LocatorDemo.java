package com.handbook.enterprise.di.locator;

import java.util.HashMap;
import java.util.Map;

/** Coding challenge: why Service Locator hides dependencies, and the DI fix. */
public class LocatorDemo {

    interface TaxRates { int gstPercent(String category); }

    /** A global registry anyone can pull from. */
    static final class ServiceLocator {
        private static final Map<Class<?>, Object> SERVICES = new HashMap<>();
        static <T> void register(Class<T> type, T impl) { SERVICES.put(type, impl); }
        static <T> T lookup(Class<T> type) {
            Object o = SERVICES.get(type);
            if (o == null) {
                throw new IllegalStateException(type.getSimpleName() + " not registered");
            }
            return type.cast(o);
        }
    }

    /** The constructor says "I need nothing", which is a lie. */
    static final class LocatorInvoice {
        long totalWithTax(String category, long paise) {
            int gst = ServiceLocator.lookup(TaxRates.class).gstPercent(category); // hidden dependency
            return paise + paise * gst / 100;
        }
    }

    /** The constructor tells the truth: you cannot even build it without TaxRates. */
    static final class InjectedInvoice {
        private final TaxRates rates;
        InjectedInvoice(TaxRates rates) { this.rates = rates; }
        long totalWithTax(String category, long paise) { return paise + paise * rates.gstPercent(category) / 100; }
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Service Locator vs Dependency Injection ===");
        LocatorInvoice invoice = new LocatorInvoice(); // compiles and constructs fine...
        try {
            invoice.totalWithTax("snacks", 10_000);
        } catch (IllegalStateException e) {
            System.out.println("Locator fails at RUNTIME, deep in a call: " + e.getMessage());
        }
        TaxRates rates = c -> c.equals("snacks") ? 12 : 5;
        System.out.println("Injected invoice: " + new InjectedInvoice(rates).totalWithTax("snacks", 10_000) + " paise");
        System.out.println("Forgetting TaxRates with DI is a COMPILE error: new InjectedInvoice() does not exist.");
    }
}
