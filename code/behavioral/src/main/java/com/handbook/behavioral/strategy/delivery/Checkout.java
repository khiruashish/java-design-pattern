package com.handbook.behavioral.strategy.delivery;

/** Context: uses whichever strategy it is given. No if/else on customer type. */
public class Checkout {

    public record Customer(String name, boolean hasPass, boolean wantsExpress) {
    }

    public static DeliveryFeeStrategy chooseFor(Customer c, int hour) { // (1)!
        if (c.hasPass()) {
            return DeliveryFeeStrategy.MEMBER_PASS;
        }
        if (c.wantsExpress()) {
            return DeliveryFeeStrategy.EXPRESS;
        }
        return hour >= 23 || hour < 5 ? DeliveryFeeStrategy.LATE_NIGHT : DeliveryFeeStrategy.STANDARD;
    }

    public String total(Customer c, int cartTotal, double km, int hour) {
        DeliveryFeeStrategy strategy = chooseFor(c, hour);
        int fee = strategy.feeInr(cartTotal, km);
        return "%-6s cart INR %4d, %.1f km, %02d:00 -> fee INR %2d, pay INR %d"
                .formatted(c.name(), cartTotal, km, hour, fee, cartTotal + fee);
    }
}
