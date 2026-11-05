package com.handbook.microservices.ambassador.token;

import com.handbook.microservices.ambassador.token.TokenAmbassador.Token;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class TokenAmbassadorDemo {

    public static void main(String[] args) {
        System.out.println("=== Challenge: Token Caching in the Ambassador (SMS gateway, 1-hour tokens) ===");
        FakeClock clock = FakeClock.at("2026-10-01T09:00:00Z");
        int[] issued = {0};
        TokenAmbassador ambassador = new TokenAmbassador(
                () -> new Token("tok-" + ++issued[0], clock.instant().plus(Duration.ofHours(1))), clock, Duration.ofMinutes(5));

        for (int minute = 0; minute <= 120; minute++) {
            for (int sms = 0; sms < 50; sms++) {
                ambassador.authorizationHeader(); // 50 SMS per minute from the app
            }
            if (minute == 0 || minute == 54 || minute == 55 || minute == 120) {
                System.out.println("t=+" + minute + "min header: " + ambassador.authorizationHeader());
            }
            clock.advance(Duration.ofMinutes(1));
        }
        System.out.println("6,050 SMS sent, token requests to the auth server: " + ambassador.tokenRequests()
                + " (refreshed 5 minutes before each expiry)");
    }
}
