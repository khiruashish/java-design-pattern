package com.handbook.microservices.backpressure.flow;

import java.util.List;
import java.util.concurrent.Flow;

/** A Reactive-Streams publisher that emits ONLY what the subscriber has requested (pull-based back-pressure). */
public final class GpsPublisher implements Flow.Publisher<String> {

    private final List<String> pings;

    public GpsPublisher(List<String> pings) {
        this.pings = pings;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {
        subscriber.onSubscribe(new Flow.Subscription() {
            private int next;
            private boolean done;

            @Override
            public void request(long n) { // (1)!
                for (long i = 0; i < n && next < pings.size(); i++) {
                    subscriber.onNext(pings.get(next++));
                }
                if (next == pings.size() && !done) {
                    done = true;
                    subscriber.onComplete();
                }
            }

            @Override
            public void cancel() {
                next = pings.size();
            }
        });
    }
}
