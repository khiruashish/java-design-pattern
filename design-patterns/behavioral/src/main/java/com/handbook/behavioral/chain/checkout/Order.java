package com.handbook.behavioral.chain.checkout;

public record Order(String id, String pincode, String sku, int qty, String coupon, int amountInr) {
}
