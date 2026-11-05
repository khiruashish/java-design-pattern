package com.handbook.microservices.backpressure.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

/** Asks for a batch, processes it, then asks for the next: it never receives more than it can handle. */
public final class BatchingSubscriber implements Flow.Subscriber<String> {

    private final int batchSize;
    private final List<String> log = new ArrayList<>();
    private final List<String> current = new ArrayList<>();
    private Flow.Subscription subscription;

    public BatchingSubscriber(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void onSubscribe(Flow.Subscription s) {
        subscription = s;
        log.add("request(" + batchSize + ")");
        s.request(batchSize); // (1)!
    }

    @Override
    public void onNext(String ping) {
        current.add(ping);
        if (current.size() == batchSize) {
            log.add("map-match " + current + " -> request(" + batchSize + ")");
            current.clear();
            subscription.request(batchSize); // (2)!
        }
    }

    @Override
    public void onError(Throwable t) {
        log.add("error " + t.getMessage());
    }

    @Override
    public void onComplete() {
        log.add("complete" + (current.isEmpty() ? "" : ", last partial batch " + current));
    }

    public List<String> log() {
        return log;
    }
}
