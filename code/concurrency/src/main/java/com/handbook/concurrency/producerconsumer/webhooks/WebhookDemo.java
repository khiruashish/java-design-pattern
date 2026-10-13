package com.handbook.concurrency.producerconsumer.webhooks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/** Payment webhooks: accept fast, process later, and push back (HTTP 503) when the buffer is full. */
public class WebhookDemo {

    record Webhook(String id, String event) {
    }

    private final BlockingQueue<Webhook> inbox = new ArrayBlockingQueue<>(3);

    int receive(Webhook w) throws InterruptedException { // the HTTP handler: producer
        boolean accepted = inbox.offer(w, 10, TimeUnit.MILLISECONDS); // (1)!
        return accepted ? 202 : 503;
    }

    void drain() { // the worker: consumer
        Webhook w;
        while ((w = inbox.poll()) != null) {
            System.out.println("  processed " + w.id() + " " + w.event());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Payments: Webhook Intake with Back-pressure ===");
        WebhookDemo service = new WebhookDemo();
        String[] events = {"payment.captured", "payment.failed", "refund.created", "payment.captured", "dispute.opened"};
        for (int i = 0; i < events.length; i++) {
            Webhook w = new Webhook("evt_" + (i + 1), events[i]);
            int status = service.receive(w);
            System.out.println("POST " + w.id() + " -> " + status + (status == 503 ? " (gateway will retry later)" : " Accepted"));
        }
        System.out.println("Worker catches up:");
        service.drain();
        System.out.println("Retry of evt_4 -> " + service.receive(new Webhook("evt_4", "payment.captured")));
    }
}
