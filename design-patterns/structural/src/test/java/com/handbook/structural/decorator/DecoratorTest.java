package com.handbook.structural.decorator;

import com.handbook.structural.decorator.caching.CachingCatalog;
import com.handbook.structural.decorator.healthcare.CompressingStore;
import com.handbook.structural.decorator.healthcare.EncryptingStore;
import com.handbook.structural.decorator.healthcare.InMemoryRecordStore;
import com.handbook.structural.decorator.healthcare.RecordStore;
import com.handbook.structural.decorator.payments.FlakyGatewayProcessor;
import com.handbook.structural.decorator.payments.FraudCheckProcessor;
import com.handbook.structural.decorator.payments.RetryingProcessor;
import com.handbook.structural.decorator.pricing.PriceCalculator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecoratorTest {

    @Test
    void retrySucceedsWithinLimitAndGivesUpAfter() {
        assertThat(new RetryingProcessor(new FlakyGatewayProcessor(2), 3).pay("O", 1)).isEqualTo("PAID O");
        assertThatThrownBy(() -> new RetryingProcessor(new FlakyGatewayProcessor(5), 3).pay("O", 1))
                .hasMessage("gateway timeout");
    }

    @Test
    void fraudCheckShortCircuitsBeforeTheGateway() {
        var neverCalled = new FlakyGatewayProcessor(Integer.MAX_VALUE);
        assertThat(new FraudCheckProcessor(neverCalled, 100).pay("O", 500)).startsWith("BLOCKED");
    }

    @Test
    void storeDecoratorsRoundTripInEitherOrder() {
        String data = "AAAABBBCCD;x";
        RecordStore a = new CompressingStore(new EncryptingStore(new InMemoryRecordStore(), "k"));
        RecordStore b = new EncryptingStore(new CompressingStore(new InMemoryRecordStore()), "k");
        a.write("1", data);
        assertThat(a.read("1")).isEqualTo(data);
        assertThatThrownBy(() -> b.write("1", data)) // encrypted Base64 may contain digits
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void pricingOrderMatters() {
        long discountFirst = PriceCalculator.base().thenMemberDiscount(10).thenGst(5).price(10_000);
        long taxFirst = PriceCalculator.base().thenGst(5).thenMemberDiscount(10).price(10_000);
        assertThat(discountFirst).isEqualTo(9_450);
        assertThat(taxFirst).isEqualTo(9_450); // percentages commute...
        long flatThenTax = PriceCalculator.base().thenFlatOff(1_000, 0).thenGst(5).price(10_000);
        long taxThenFlat = PriceCalculator.base().thenGst(5).thenFlatOff(1_000, 0).price(10_000);
        assertThat(flatThenTax).isNotEqualTo(taxThenFlat); // ...flat amounts do not
    }

    @Test
    void cachingCallsTheSourceOncePerKey() {
        AtomicInteger calls = new AtomicInteger();
        CachingCatalog c = new CachingCatalog(sku -> sku + calls.incrementAndGet());
        c.describe("a");
        c.describe("a");
        assertThat(calls).hasValue(1);
        assertThat(c.stats()).isEqualTo("hits=1 misses=1");
    }
}
