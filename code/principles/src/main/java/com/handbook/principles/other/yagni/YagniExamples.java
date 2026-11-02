package com.handbook.principles.other.yagni;

import java.util.Map;

/** YAGNI: the requirement today is "convert INR to USD at a configured rate". */
public final class YagniExamples {
    private YagniExamples() { }

    /** Speculative: plugin registry, provider SPI, caching hooks, multi-hop conversion... none of it requested. */
    public interface RateProvider { double rate(String from, String to); }

    public interface RateCache { Double lookup(String pair); void store(String pair, double rate); }

    public interface ConversionListener { void converted(String from, String to, long in, long out); }

    public static final class OverEngineeredConverter {
        private final Map<String, RateProvider> providers;
        private final RateCache cache;
        private final ConversionListener listener;

        public OverEngineeredConverter(Map<String, RateProvider> providers, RateCache cache, ConversionListener listener) {
            this.providers = providers;
            this.cache = cache;
            this.listener = listener;
        }

        public long convert(String providerName, String from, String to, long minorUnits) {
            String pair = from + "->" + to;
            Double rate = cache.lookup(pair);
            if (rate == null) {
                rate = providers.get(providerName).rate(from, to);
                cache.store(pair, rate);
            }
            long out = Math.round(minorUnits * rate);
            listener.converted(from, to, minorUnits, out);
            return out;
        }
    }

    /** Just enough: one rate, one method. Easy to extend WHEN a real second requirement appears. */
    public record InrToUsd(long usdCentsPerHundredRupees) {
        public long convertPaise(long paise) {
            return Math.round(paise * (double) usdCentsPerHundredRupees / 10_000);
        }
    }
}
