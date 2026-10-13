package com.handbook.creational.factorymethod;

import com.handbook.creational.factorymethod.payments.CardProcessor;
import com.handbook.creational.factorymethod.payments.PaymentMethod;
import com.handbook.creational.factorymethod.payments.PaymentProcessorFactory;
import com.handbook.creational.factorymethod.payments.PaymentResult;
import com.handbook.creational.factorymethod.payments.UnsupportedPaymentMethodException;
import com.handbook.creational.factorymethod.payments.UpiProcessor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentProcessorFactoryTest {

    private final PaymentProcessorFactory factory = new PaymentProcessorFactory()
            .register(PaymentMethod.UPI, UpiProcessor::new)
            .register(PaymentMethod.CARD, CardProcessor::new);

    @Test
    void createsRegisteredProcessor() {
        assertThat(factory.create(PaymentMethod.CARD)).isInstanceOf(CardProcessor.class);
        assertThat(factory.create(PaymentMethod.CARD).pay("O1", 1000))
                .isEqualTo(new PaymentResult(true, 20, "ref=CARD-O1"));
    }

    @Test
    void returnsANewInstanceEachTime() {
        assertThat(factory.create(PaymentMethod.UPI)).isNotSameAs(factory.create(PaymentMethod.UPI));
    }

    @Test
    void canSwapInATestDouble() {
        factory.register(PaymentMethod.UPI, () -> (orderId, amount) -> PaymentResult.failed("bank down"));
        assertThat(factory.create(PaymentMethod.UPI).pay("O1", 10).success()).isFalse();
    }

    @Test
    void unknownMethodFailsFast() {
        assertThatThrownBy(() -> factory.create(PaymentMethod.NET_BANKING))
                .isInstanceOf(UnsupportedPaymentMethodException.class)
                .hasMessage("No processor registered for NET_BANKING");
    }
}
