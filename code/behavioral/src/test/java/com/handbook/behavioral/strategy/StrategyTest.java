package com.handbook.behavioral.strategy;

import com.handbook.behavioral.strategy.delivery.Checkout;
import com.handbook.behavioral.strategy.delivery.DeliveryFeeStrategy;
import com.handbook.behavioral.strategy.matching.MatchingStrategy;
import com.handbook.behavioral.strategy.routing.GatewayRouter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StrategyTest {

    @Test
    void eachFeeStrategyInIsolation() {
        assertThat(DeliveryFeeStrategy.STANDARD.feeInr(198, 1)).isEqualTo(30);
        assertThat(DeliveryFeeStrategy.STANDARD.feeInr(199, 1)).isZero();
        assertThat(DeliveryFeeStrategy.EXPRESS.feeInr(100, 3.5)).isEqualTo(59);
        assertThat(DeliveryFeeStrategy.LATE_NIGHT.feeInr(100, 1)).isEqualTo(55);
    }

    @Test
    void passHoldersAlwaysGetFreeDelivery() {
        var c = new Checkout.Customer("x", true, true);
        assertThat(Checkout.chooseFor(c, 2)).isSameAs(DeliveryFeeStrategy.MEMBER_PASS);
    }

    @Test
    void matchingReturnsEmptyWhenNoDriverQualifies() {
        var far = List.of(new MatchingStrategy.Driver("a", 9, 5, 1));
        assertThat(MatchingStrategy.bestRatedWithin(2).pick(far)).isEmpty();
        assertThat(MatchingStrategy.nearest().pick(far)).isPresent();
    }

    @Test
    void expectedCostAvoidsUnreliableCheapGateway() {
        var gws = List.of(new GatewayRouter.Gateway("cheap", 1.0, 0.5), new GatewayRouter.Gateway("solid", 2.0, 0.99));
        assertThat(new GatewayRouter(GatewayRouter.Strategy.LOWEST_FEE).route(gws).name()).isEqualTo("cheap");
        assertThat(new GatewayRouter(GatewayRouter.Strategy.LOWEST_EXPECTED_COST).route(gws).name()).isEqualTo("solid");
    }
}
