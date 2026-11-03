package com.handbook.microservices.deployment.bluegreen;

public class BlueGreenDemo {

    public static void main(String[] args) {
        System.out.println("=== E-commerce: Blue-Green Release of the Checkout Service ===");
        BlueGreenRouter router = new BlueGreenRouter(cart -> "total INR 1,240 (v41)");
        System.out.println("Before release   -> " + router.handle("cart-1"));

        router.deployToIdle(cart -> cart.equals("cart-with-coupon") ? "500 NullPointerException (v42)" : "total INR 1,240 (v42)");
        System.out.println("Smoke test green -> " + router.smokeTestIdle("cart-1") + " (no customer traffic yet)");

        router.switchTraffic();
        System.out.println("After switch     -> " + router.handle("cart-1"));
        System.out.println("Coupon user      -> " + router.handle("cart-with-coupon") + "  <- bug found in production");

        router.switchTraffic(); // rollback = flip back; blue is still running v41, warm and unchanged
        System.out.println("Rollback (1 step)-> " + router.handle("cart-with-coupon"));
    }
}
