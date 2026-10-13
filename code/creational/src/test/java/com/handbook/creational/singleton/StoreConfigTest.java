package com.handbook.creational.singleton;

import com.handbook.creational.singleton.grocery.CheckoutService;
import com.handbook.creational.singleton.grocery.StoreConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoreConfigTest {

    @Test
    void enumSingletonIsTheSameInstance() {
        assertThat(StoreConfig.valueOf("INSTANCE")).isSameAs(StoreConfig.INSTANCE);
    }

    @Test
    void deliveryIsFreeAboveThreshold() {
        CheckoutService checkout = new CheckoutService();
        assertThat(checkout.deliveryFee(350)).isEqualTo(30);
        assertThat(checkout.deliveryFee(499)).isZero();
    }

    @Test
    void unknownKeyFailsFast() {
        assertThatThrownBy(() -> StoreConfig.INSTANCE.getInt("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing");
    }
}
