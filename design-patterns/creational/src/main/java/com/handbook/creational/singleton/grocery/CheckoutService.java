package com.handbook.creational.singleton.grocery;

public class CheckoutService {

    private final StoreConfig config = StoreConfig.INSTANCE; // (1)!

    public int deliveryFee(int subtotalInr) {
        int freeAbove = config.getInt("free.delivery.above.inr");
        return subtotalInr >= freeAbove ? 0 : config.getInt("delivery.fee.inr");
    }

    public String checkout(int cartId, int subtotalInr) {
        int fee = deliveryFee(subtotalInr);
        return "Cart #%d subtotal=INR %d -> delivery fee=INR %d, total=INR %d"
                .formatted(cartId, subtotalInr, fee, subtotalInr + fee);
    }

    StoreConfig config() {
        return config;
    }
}
