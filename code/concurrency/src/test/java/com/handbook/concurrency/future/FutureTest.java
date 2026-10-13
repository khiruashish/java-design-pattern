package com.handbook.concurrency.future;

import com.handbook.concurrency.future.mirrors.FirstSuccessful;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FutureTest {

    @Test
    @Timeout(5)
    void completeOnTimeoutSuppliesFallback() {
        CompletableFuture<String> never = new CompletableFuture<>();
        assertThat(never.completeOnTimeout("fallback", 50, TimeUnit.MILLISECONDS).join()).isEqualTo("fallback");
    }

    @Test
    void exceptionallyRecovers() {
        CompletableFuture<Integer> f = CompletableFuture.<Integer>failedFuture(new RuntimeException("x")).exceptionally(e -> -1);
        assertThat(f.join()).isEqualTo(-1);
    }

    @Test
    void firstSuccessfulIgnoresEarlyFailures() {
        var failed = CompletableFuture.<String>failedFuture(new RuntimeException("down"));
        var ok = CompletableFuture.completedFuture("ok");
        assertThat(FirstSuccessful.of(List.of(failed, ok)).join()).isEqualTo("ok");
    }

    @Test
    void firstSuccessfulFailsWhenAllFail() {
        var a = CompletableFuture.<String>failedFuture(new RuntimeException("a"));
        var b = CompletableFuture.<String>failedFuture(new RuntimeException("b"));
        assertThatThrownBy(() -> FirstSuccessful.of(List.of(a, b)).join())
                .isInstanceOf(CompletionException.class).hasMessageContaining("all 2 attempts failed");
    }
}
