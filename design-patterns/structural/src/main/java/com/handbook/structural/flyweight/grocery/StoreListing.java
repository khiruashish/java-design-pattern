package com.handbook.structural.flyweight.grocery;

/** Extrinsic, per-store data, plus a reference to the shared product info. */
public record StoreListing(String storeId, ProductInfo product, int priceInr, int stock) {
}
