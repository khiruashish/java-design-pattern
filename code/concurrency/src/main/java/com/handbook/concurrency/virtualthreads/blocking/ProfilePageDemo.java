package com.handbook.concurrency.virtualthreads.blocking;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Challenge solution: an async page written as plain blocking code on virtual threads. */
public class ProfilePageDemo {

    static String user(String id) throws InterruptedException { Thread.sleep(40); return "Asha (Gold member)"; }

    static List<String> orders(String id) throws InterruptedException { Thread.sleep(60); return List.of("ORD-1", "ORD-2"); }

    static List<String> recommendations(String id) throws InterruptedException { Thread.sleep(2_000); return List.of("slow"); }

    public static void main(String[] args) throws Exception {
        try (ExecutorService io = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> user = io.submit(() -> user("U1"));
            Future<List<String>> orders = io.submit(() -> orders("U1"));
            Future<List<String>> recs = io.submit(() -> recommendations("U1"));

            List<String> recommended;
            try {
                recommended = recs.get(200, TimeUnit.MILLISECONDS); // optional section: short timeout
            } catch (TimeoutException e) {
                recs.cancel(true); // interrupts the virtual thread doing the slow call
                recommended = List.of("Trending: Monsoon Express");
            }
            System.out.println("User            : " + user.get());          // plain blocking calls,
            System.out.println("Recent orders   : " + orders.get());        // normal stack traces,
            System.out.println("Recommendations : " + recommended);         // normal try/catch
            System.out.println("Slow call cancelled: " + recs.isCancelled());
        } catch (ExecutionException e) {
            System.out.println("Essential call failed: " + e.getCause());
        }
    }
}
