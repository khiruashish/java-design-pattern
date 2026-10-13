package com.handbook.microservices.ratelimit.slidinglog;

import com.handbook.microservices.support.FakeClock;

import java.time.Duration;

public class OtpDemo {

    public static void main(String[] args) {
        FakeClock clock = FakeClock.at("2026-10-13T09:00:00Z");
        var limiter = new OtpRateLimiter(3, Duration.ofMinutes(10), clock);

        System.out.println("=== Fintech login: max 3 OTP SMS per phone per 10 minutes ===");
        int[] minutes = {0, 1, 2, 3, 9, 10, 11};
        int previous = 0;
        for (int minute : minutes) {
            clock.advance(Duration.ofMinutes(minute - previous));
            previous = minute;
            long wait = limiter.tryRequest("+91-98450-00000");
            System.out.printf("t=%2d min  send OTP -> %s%n", minute,
                    wait == 0 ? "SMS sent" : "429, try again in " + wait + " s");
        }
    }
}
