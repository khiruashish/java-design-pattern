package com.handbook.microservices.gateway.openbanking;

import com.handbook.microservices.gateway.openbanking.OpenBankingGateway.Partner;
import com.handbook.microservices.support.FakeClock;
import com.handbook.microservices.support.Http.Request;
import com.handbook.microservices.support.Http.Response;

import java.util.Map;

public class OpenBankingDemo {

    public static void main(String[] args) {
        System.out.println("=== Banking: Open-Banking API Gateway for Fintech Partners ===");
        FakeClock clock = FakeClock.at("2026-10-13T10:00:05Z");
        OpenBankingGateway gw = new OpenBankingGateway(
                Map.of("key-budgetapp", new Partner("BudgetApp", 3), "key-lender", new Partner("QuickLender", 100)),
                r -> Response.of(200, "balance for " + r.header("X-Partner")), clock);

        Map<String, String> budgetApp = Map.of("X-Api-Key", "key-budgetapp");
        for (int i = 1; i <= 4; i++) {
            System.out.println("BudgetApp call " + i + " -> " + gw.handle(Request.get("/accounts/SB-1/balance", budgetApp)));
            clock.advanceMillis(5_000);
        }
        System.out.println("Unknown key       -> " + gw.handle(Request.get("/accounts/SB-1/balance", Map.of("X-Api-Key", "stolen"))));
        clock.advanceMillis(45_000); // next minute
        System.out.println("Next minute       -> " + gw.handle(Request.get("/accounts/SB-1/balance", budgetApp)));
    }
}
