package com.handbook.creational.objectpool.streaming;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamingDemo {

    public static void main(String[] args) {
        System.out.println("=== Streaming: Transcoding Uploads with 3 Pooled Workers ===");
        TranscoderPool pool = new TranscoderPool(3);

        try (ExecutorService uploads = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 30; i++) {
                String video = "upload-" + i + ".mp4";
                uploads.submit(() -> {
                    pool.run(video);
                    return null;
                });
            }
        } // waits for all 30 uploads

        System.out.println("Uploads transcoded : " + pool.totalJobs());
        System.out.println("Transcoders created: " + pool.created());
        System.out.println("Never more than 3 busy at once: " + (pool.peakInUse() <= 3));
    }
}
