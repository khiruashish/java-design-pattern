package com.handbook.concurrency.guarded.buffer;

public class BufferDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Streaming: Start Playback Only When 6 Seconds Are Buffered ===");
        PlaybackBuffer good = new PlaybackBuffer();
        Thread network = Thread.ofVirtual().start(() -> {
            for (int i = 0; i < 4; i++) {
                good.append(2); // 2-second segments arriving
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        System.out.println("Fast network: playable within 2s? " + good.awaitPlayable(6, 2_000));
        network.join();

        PlaybackBuffer slow = new PlaybackBuffer();
        slow.append(2); // only one segment before the network drops
        boolean playable = slow.awaitPlayable(6, 150);
        System.out.println("Weak network: playable within 150ms? " + playable + " -> switch to 480p and retry");
    }
}
