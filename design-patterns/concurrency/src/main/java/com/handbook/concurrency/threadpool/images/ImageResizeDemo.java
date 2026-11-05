package com.handbook.concurrency.threadpool.images;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** CPU-bound work: a pool sized to the number of cores, with a bounded queue. */
public class ImageResizeDemo {

    static int resize(int width) { // stand-in for CPU-heavy pixel work
        long checksum = 0;
        for (int i = 0; i < 200_000; i++) {
            checksum += (i * 31L) ^ width;
        }
        return checksum != 0 ? width / 4 : 0; // the thumbnail width
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== E-commerce: Generating Thumbnails for 40 Product Images ===");
        int cores = Runtime.getRuntime().availableProcessors();
        AtomicInteger threadNo = new AtomicInteger();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                cores, cores,                                    // (1)!
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),                   // (2)!
                r -> new Thread(r, "resizer-" + threadNo.incrementAndGet()), // (3)!
                new ThreadPoolExecutor.CallerRunsPolicy());      // (4)!

        List<Callable<Integer>> jobs = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            int width = 2_000 + i * 40;
            jobs.add(() -> resize(width));
        }
        int totalThumbWidth = 0;
        for (Future<Integer> f : pool.invokeAll(jobs)) { // waits for all
            totalThumbWidth += f.get();
        }
        pool.shutdown();
        System.out.println("Images resized          : " + jobs.size());
        System.out.println("Sum of thumbnail widths : " + totalThumbWidth);
        System.out.println("Threads never exceeded cores: " + (pool.getLargestPoolSize() <= cores));
        System.out.println("Pool terminated cleanly : " + pool.awaitTermination(5, TimeUnit.SECONDS));
    }
}
