package com.handbook.creational.builder.api;

import java.time.Duration;

public class ApiRequestDemo {

    public static void main(String[] args) {
        ApiRequest base = ApiRequest.builder("https://api.example.com/payments")
                .header("Authorization", "Bearer t0k3n")
                .timeout(Duration.ofSeconds(5))
                .build();
        System.out.println(base.method() + " " + base.url() + " timeout=" + base.timeout().toSeconds() + "s");

        ApiRequest charge = base.toBuilder()
                .post("{\"amount\":499}")
                .header("Idempotency-Key", "ORD-77")
                .retries(3)
                .build();
        System.out.println(charge.method() + " retries=" + charge.maxRetries() + " headers=" + charge.headers().keySet().stream().sorted().toList());
        System.out.println("Original unchanged? " + base.method().equals("GET"));

        try {
            base.toBuilder().post("{}").retries(2).build();
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
}
