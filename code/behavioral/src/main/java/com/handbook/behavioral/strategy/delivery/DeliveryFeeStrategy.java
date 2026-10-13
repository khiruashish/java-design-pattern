package com.handbook.behavioral.strategy.delivery;

/** Strategy: one way of computing a delivery fee. */
@FunctionalInterface
public interface DeliveryFeeStrategy {

    int feeInr(int cartTotalInr, double distanceKm);

    DeliveryFeeStrategy STANDARD = (total, km) -> total >= 199 ? 0 : 30; // (1)!
    DeliveryFeeStrategy EXPRESS = (total, km) -> 49 + (km > 3 ? 10 : 0);
    DeliveryFeeStrategy MEMBER_PASS = (total, km) -> 0;
    DeliveryFeeStrategy LATE_NIGHT = (total, km) -> STANDARD.feeInr(total, km) + 25;
}
