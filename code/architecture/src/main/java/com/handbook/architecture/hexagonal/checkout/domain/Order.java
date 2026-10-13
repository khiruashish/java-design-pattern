package com.handbook.architecture.hexagonal.checkout.domain;

/** Core domain object. No annotations, no framework types. */
public record Order(String id, String customerId, long totalPaise, Status status) {

    public enum Status { PAID, PAYMENT_DECLINED }
}
