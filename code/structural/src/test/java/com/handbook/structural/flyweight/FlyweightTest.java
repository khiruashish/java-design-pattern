package com.handbook.structural.flyweight;

import com.handbook.structural.flyweight.editor.TextStyle;
import com.handbook.structural.flyweight.grocery.Catalog;
import com.handbook.structural.flyweight.ridemap.IconFactory;
import com.handbook.structural.flyweight.social.Hashtag;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlyweightTest {

    @Test
    void factoryReturnsSharedInstances() {
        IconFactory f = new IconFactory();
        assertThat(f.icon("Mini")).isSameAs(f.icon("Mini"));
        assertThat(f.icon("Mini")).isNotSameAs(f.icon("Auto"));
        assertThat(f.distinctIcons()).isEqualTo(2);
    }

    @Test
    void equalStylesAreInterned() {
        assertThat(TextStyle.of("A", 1, true, false, "c")).isSameAs(TextStyle.of("A", 1, true, false, "c"));
    }

    @Test
    void catalogLoadsEachProductOnce() {
        Catalog c = new Catalog();
        c.product("X");
        c.product("X");
        assertThat(c.loads()).isEqualTo(1);
    }

    @Test
    void hashtagsInternUnderConcurrency() {
        Set<Hashtag> seen = ConcurrentHashMap.newKeySet();
        try (ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000; i++) {
                ex.submit(() -> seen.add(Hashtag.of("#Concurrency")));
            }
        }
        assertThat(seen).hasSize(1);
        assertThatThrownBy(() -> Hashtag.of("no-hash")).isInstanceOf(IllegalArgumentException.class);
    }
}
