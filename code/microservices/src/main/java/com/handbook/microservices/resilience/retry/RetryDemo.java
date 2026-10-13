package com.handbook.microservices.resilience.retry;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RetryDemo {

    public static void main(String[] args) {
        long[] slept = {0};
        Retry retry = new Retry(4, 100, 2_000, HttpError::isTransient, new Random(42), ms -> slept[0] += ms);

        System.out.println("=== Grocery: reserve stock in the inventory service ===");
        Iterator<Integer> statuses = List.of(503, 503, 200).iterator();
        String result = retry.call(() -> respond(statuses.next()));
        System.out.println("Result: " + result + " after sleeping " + slept[0] + " ms in total");

        System.out.println("-- a validation error is never retried --");
        Iterator<Integer> badRequest = List.of(400, 200).iterator();
        try {
            retry.call(() -> respond(badRequest.next()));
        } catch (HttpError e) {
            System.out.println("Gave up immediately: " + e.getMessage());
        }

        System.out.println("-- a long outage exhausts the attempts --");
        try {
            retry.call(() -> respond(503));
        } catch (HttpError e) {
            System.out.println("Gave up after 4 attempts: " + e.getMessage());
        }
    }

    private static String respond(int status) {
        if (status != 200) {
            throw new HttpError(status, status == 400 ? "quantity must be positive" : "service unavailable");
        }
        return "reserved MANGO-1KG x2";
    }
}
