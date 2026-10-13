package com.handbook.microservices.tracing.threads;

import com.handbook.microservices.tracing.core.TraceContext;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadContextDemo {

    public static void main(String[] args) {
        TraceContext request = TraceContext.newRoot(new Random(3));
        ContextPropagatingExecutor.CURRENT.set(request);

        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            System.out.println("=== Streaming: parallel home-screen fetches keep the trace ===");
            System.out.println("request thread trace: " + request.traceId().substring(0, 8) + "...");
            System.out.println("plain executor      : " + fetchAll(pool));
            System.out.println("propagating executor: " + fetchAll(new ContextPropagatingExecutor(pool)));
        } finally {
            ContextPropagatingExecutor.CURRENT.remove();
        }
    }

    private static List<String> fetchAll(Executor executor) {
        List<CompletableFuture<String>> rows = List.of("continue-watching", "trending", "for-you").stream()
                .map(row -> CompletableFuture.supplyAsync(() -> {
                    TraceContext ctx = ContextPropagatingExecutor.CURRENT.get();
                    return row + "=" + (ctx == null ? "NO TRACE" : ctx.traceId().substring(0, 8));
                }, executor))
                .toList();
        return rows.stream().map(CompletableFuture::join).toList();
    }
}
