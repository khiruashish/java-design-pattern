package com.handbook.architecture.modular.shop.shared;

/** A published event: part of the ordering module's public contract. */
public record OrderPlaced(String orderId, String customerId, long totalPaise) {
}
