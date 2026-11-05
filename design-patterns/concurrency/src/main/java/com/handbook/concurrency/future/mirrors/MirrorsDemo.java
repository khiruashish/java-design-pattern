package com.handbook.concurrency.future.mirrors;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MirrorsDemo {

    static CompletableFuture<String> download(ExecutorService io, String mirror, long ms, boolean fails) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (fails) {
                throw new IllegalStateException(mirror + " returned 500");
            }
            return "app-v2.apk from " + mirror;
        }, io);
    }

    public static void main(String[] args) {
        try (ExecutorService io = Executors.newVirtualThreadPerTaskExecutor()) {
            // The fastest mirror fails; anyOf() would report that failure. FirstSuccessful waits for a good one.
            String got = FirstSuccessful.of(List.of(
                    download(io, "mirror-mumbai", 20, true),
                    download(io, "mirror-singapore", 80, false),
                    download(io, "mirror-frankfurt", 150, false))).join();
            System.out.println("Downloaded: " + got);

            try {
                FirstSuccessful.of(List.of(download(io, "a", 10, true), download(io, "b", 20, true))).join();
            } catch (CompletionException e) {
                System.out.println("All mirrors down: " + e.getCause().getMessage());
            }
        }
    }
}
