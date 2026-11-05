package com.handbook.behavioral.strategy.routing;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

/** Context that routes each payment to a gateway using a pluggable scoring strategy. */
public class GatewayRouter {

    public record Gateway(String name, double feePercent, double successRate) {
    }

    /** Strategy as a function: lower score wins. */
    public enum Strategy {
        LOWEST_FEE(g -> g.feePercent()),
        HIGHEST_SUCCESS(g -> -g.successRate()),
        LOWEST_EXPECTED_COST(g -> g.feePercent() + (1 - g.successRate()) * 10); // (1)!

        private final ToDoubleFunction<Gateway> score;

        Strategy(ToDoubleFunction<Gateway> score) {
            this.score = score;
        }
    }

    private Strategy strategy;

    public GatewayRouter(Strategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy) { // (2)!
        this.strategy = strategy;
    }

    public Gateway route(List<Gateway> gateways) {
        return gateways.stream().min(Comparator.comparingDouble(strategy.score)).orElseThrow();
    }
}
