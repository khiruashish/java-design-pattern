package com.handbook.behavioral.nullobject.coupon;

public class CouponDemo {

    static String checkout(int subtotal, String enteredCode) {
        Coupon coupon = Coupon.lookup(enteredCode);
        int discount = coupon.discountInr(subtotal); // no null check anywhere
        return "INR %4d with %-38s -> pay INR %d".formatted(subtotal, coupon.label(), subtotal - discount);
    }

    public static void main(String[] args) {
        System.out.println("=== Grocery: Coupons without Null Checks ===");
        System.out.println(checkout(800, "fresh20"));
        System.out.println(checkout(300, "FRESH20"));
        System.out.println(checkout(600, "SAVE50"));
        System.out.println(checkout(400, "SAVE50"));
        System.out.println(checkout(600, "EXPIRED10"));
        System.out.println(checkout(600, null));
    }
}
