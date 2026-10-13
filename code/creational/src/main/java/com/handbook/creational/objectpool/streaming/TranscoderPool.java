package com.handbook.creational.objectpool.streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/** Fixed-size, pre-warmed pool: all workers are created up front. */
public class TranscoderPool {

    private final BlockingQueue<Transcoder> available;
    private final List<Transcoder> all = new ArrayList<>();
    private final AtomicInteger inUse = new AtomicInteger();
    private final AtomicInteger peakInUse = new AtomicInteger();

    public TranscoderPool(int size) {
        available = new ArrayBlockingQueue<>(size);
        for (int i = 1; i <= size; i++) { // (1)!
            Transcoder t = new Transcoder(i);
            all.add(t);
            available.add(t);
        }
    }

    public void run(String video) throws InterruptedException {
        Transcoder t = available.take(); // (2)!
        peakInUse.accumulateAndGet(inUse.incrementAndGet(), Math::max);
        try {
            t.transcode(video);
        } finally {
            inUse.decrementAndGet();
            available.put(t); // (3)!
        }
    }

    public int created() { return all.size(); }

    public int peakInUse() { return peakInUse.get(); }

    public int totalJobs() { return all.stream().mapToInt(Transcoder::jobsDone).sum(); }
}
