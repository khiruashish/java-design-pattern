package com.handbook.microservices;

import com.handbook.microservices.eventsourcing.account.Account;
import com.handbook.microservices.eventsourcing.account.Account.Deposited;
import com.handbook.microservices.eventsourcing.account.Account.Event;
import com.handbook.microservices.eventsourcing.account.Account.Opened;
import com.handbook.microservices.eventsourcing.account.AccountRepository;
import com.handbook.microservices.eventsourcing.account.EventStore;
import com.handbook.microservices.eventsourcing.upcasting.Upcaster;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventSourcingTest {

    @Test
    void stateIsRebuiltFromEventsWithOrWithoutSnapshots() {
        EventStore<Event> store = new EventStore<>();
        store.append("A", 0, List.of(new Opened("x")));
        var repo = new AccountRepository(store, 2);
        repo.execute("A", a -> a.deposit(100));
        repo.execute("A", a -> a.deposit(50));
        assertThat(repo.load("A").balance()).isEqualTo(150);
        assertThat(Account.replay(null, store.read("A", 0)).balance()).isEqualTo(150);
    }

    @Test
    void staleWriterIsRejected() {
        EventStore<Event> store = new EventStore<>();
        store.append("A", 0, List.of(new Opened("x")));
        store.append("A", 1, List.of(new Deposited(1)));
        assertThatThrownBy(() -> store.append("A", 1, List.of(new Deposited(1))))
                .isInstanceOf(EventStore.ConcurrencyException.class);
    }

    @Test
    void upcasterConvertsRupeeStringsToPaise() {
        Map<String, Object> v1 = new java.util.LinkedHashMap<>(Map.of("type", "PriceSet", "version", 1, "price", "1.05"));
        assertThat(Upcaster.upcast(v1)).containsEntry("amountPaise", 105L).containsEntry("version", 2);
    }
}
