package com.handbook.microservices.competingconsumers.visibility;

import com.handbook.microservices.competingconsumers.visibility.VisibilityQueue.Message;
import com.handbook.microservices.support.FakeClock;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class WebhookDemo {

    public static void main(String[] args) {
        System.out.println("=== Payments: A Worker Crashes Mid-Webhook; Another Finishes It ===");
        FakeClock clock = FakeClock.at("2026-08-15T10:00:00Z");
        VisibilityQueue queue = new VisibilityQueue(clock, Duration.ofSeconds(30));
        queue.send("evt_91", "payment.captured pay_ABC INR 1,499");
        Set<String> settledPayments = new HashSet<>(); // the idempotency record

        Message m1 = queue.receive().orElseThrow();
        System.out.println("worker-A received " + m1.id() + " (attempt " + m1.receiveCount() + ") ... crashed before ack");
        System.out.println("worker-B polls at +5s  : " + queue.receive().map(Message::id).orElse("nothing (message still hidden)"));

        clock.advance(Duration.ofSeconds(31));
        Message m2 = queue.receive().orElseThrow();
        boolean firstTime = settledPayments.add("pay_ABC");
        queue.ack(m2.id());
        System.out.println("worker-B polls at +36s : " + m2.id() + " (attempt " + m2.receiveCount() + "), settled=" + firstTime + ", acked");
        System.out.println("worker-C polls later   : " + queue.receive().map(Message::id).orElse("nothing (done)"));
    }
}
