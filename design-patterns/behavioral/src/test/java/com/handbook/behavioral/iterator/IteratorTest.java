package com.handbook.behavioral.iterator;

import com.handbook.behavioral.iterator.feed.FeedMerger;
import com.handbook.behavioral.iterator.playlist.Playlist;
import com.handbook.behavioral.iterator.ring.RingBuffer;
import com.handbook.behavioral.iterator.statement.StatementApi;
import com.handbook.behavioral.iterator.statement.Transactions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IteratorTest {

    @Test
    void paginationIsLazyAndComplete() {
        StatementApi api = new StatementApi();
        Transactions txns = new Transactions(api, "A");
        assertThat(txns.stream().limit(2).count()).isEqualTo(2);
        assertThat(api.calls()).isEqualTo(1);
        assertThat(txns.stream().count()).isEqualTo(8);
        Iterator<StatementApi.Txn> it = txns.iterator();
        while (it.hasNext()) {
            it.next();
        }
        assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void feedIsSortedNewestFirst() {
        var a = List.of(new FeedMerger.Post(5, "a", ""), new FeedMerger.Post(1, "a", ""));
        var b = List.of(new FeedMerger.Post(4, "b", ""), new FeedMerger.Post(3, "b", ""));
        FeedMerger m = new FeedMerger(List.of(a.iterator(), b.iterator()));
        List<Long> times = new ArrayList<>();
        m.forEachRemaining(p -> times.add(p.timestamp()));
        assertThat(times).containsExactly(5L, 4L, 3L, 1L);
    }

    @Test
    void shuffleIsReproducibleWithSeed() {
        Playlist p = new Playlist(List.of("a", "b", "c", "d"));
        List<String> first = new ArrayList<>();
        List<String> second = new ArrayList<>();
        p.play(Playlist.Mode.SHUFFLE, 7).forEachRemaining(first::add);
        p.play(Playlist.Mode.SHUFFLE, 7).forEachRemaining(second::add);
        assertThat(first).isEqualTo(second).containsExactlyInAnyOrder("a", "b", "c", "d");
    }

    @Test
    void ringBufferKeepsNewest() {
        RingBuffer<Integer> r = new RingBuffer<>(2);
        r.add(1);
        r.add(2);
        r.add(3);
        List<Integer> out = new ArrayList<>();
        r.forEach(out::add);
        assertThat(out).containsExactly(2, 3);
    }
}
