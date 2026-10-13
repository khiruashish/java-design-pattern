package com.handbook.behavioral.strategy.routing;

import com.handbook.behavioral.strategy.routing.GatewayRouter.Gateway;
import com.handbook.behavioral.strategy.routing.GatewayRouter.Strategy;

import java.util.List;

public class RoutingDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: Smart Gateway Routing ===");
        List<Gateway> gateways = List.of(
                new Gateway("PayFast", 1.6, 0.91),
                new Gateway("SecurePay", 2.1, 0.985),
                new Gateway("ValueGate", 1.8, 0.97));

        GatewayRouter router = new GatewayRouter(Strategy.LOWEST_FEE);
        for (Strategy s : Strategy.values()) {
            router.setStrategy(s);
            System.out.printf("%-20s -> %s%n", s, router.route(gateways).name());
        }

        System.out.println("ValueGate success rate drops to 0.80 during a bank outage:");
        List<Gateway> degraded = List.of(gateways.get(0), gateways.get(1), new Gateway("ValueGate", 1.8, 0.80));
        router.setStrategy(Strategy.LOWEST_EXPECTED_COST);
        System.out.printf("%-20s -> %s%n", Strategy.LOWEST_EXPECTED_COST, router.route(degraded).name());
    }
}
