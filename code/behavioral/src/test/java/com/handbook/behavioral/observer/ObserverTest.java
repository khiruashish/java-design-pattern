package com.handbook.behavioral.observer;

import com.handbook.behavioral.observer.bus.EventBus;
import com.handbook.behavioral.observer.orders.OrderEvents;
import com.handbook.behavioral.observer.trading.PriceFeed;
import com.handbook.behavioral.observer.tracking.DeliveryTracker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ObserverTest {

    @Test
    void unsubscribeStopsNotifications() {
        PriceFeed feed = new PriceFeed();
        List<Double> seen = new ArrayList<>();
        Runnable unsubscribe = feed.subscribe((s, p) -> seen.add(p));
        feed.tick("X", 1);
        unsubscribe.run();
        feed.tick("X", 2);
        assertThat(seen).containsExactly(1.0);
    }

    @Test
    void listenerMayUnsubscribeDuringNotification() {
        PriceFeed feed = new PriceFeed();
        Runnable[] holder = new Runnable[1];
        holder[0] = feed.subscribe((s, p) -> holder[0].run());
        feed.tick("X", 1); // no ConcurrentModificationException thanks to List.copyOf
    }

    @Test
    void failingListenerDoesNotBlockOthers() {
        OrderEvents events = new OrderEvents();
        AtomicInteger delivered = new AtomicInteger();
        events.onOrderPlaced(e -> { throw new IllegalStateException("boom"); });
        events.onOrderPlaced(e -> delivered.incrementAndGet());
        events.publish(new OrderEvents.OrderPlaced("1", "e", List.of(), 1));
        assertThat(delivered).hasValue(1);
    }

    @Test
    void trackerReleasesObserversAfterDelivery() {
        DeliveryTracker t = new DeliveryTracker("x");
        t.attach(new DeliveryTracker.Observer() {
            public void locationChanged(String id, double km) { }
            public void delivered(String id) { }
        });
        t.markDelivered();
        assertThat(t.observerCount()).isZero();
    }

    record Ping(int n) {
    }

    @Test
    void eventBusIsThreadSafe() {
        EventBus bus = new EventBus();
        AtomicInteger count = new AtomicInteger();
        bus.subscribe(Ping.class, p -> count.incrementAndGet());
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                int n = i;
                ex.submit(() -> bus.publish(new Ping(n)));
            }
        }
        assertThat(count).hasValue(1_000);
    }
}
