package com.handbook.concurrency.producerconsumer.picking;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** Dark store: the order intake produces pick tasks; 3 pickers consume them. */
public class PickingDemo {

    record PickTask(String orderId, int items) {
    }

    private static final PickTask POISON_PILL = new PickTask("STOP", 0); // (1)!

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Grocery Dark Store: Order Picking ===");
        BlockingQueue<PickTask> queue = new ArrayBlockingQueue<>(5); // (2)!
        AtomicInteger maxDepth = new AtomicInteger();
        Map<String, Integer> pickedBy = new ConcurrentHashMap<>();
        AtomicInteger itemsPicked = new AtomicInteger();
        int pickers = 3;

        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int p = 1; p <= pickers; p++) {
                String picker = "picker-" + p;
                pool.submit(() -> {
                    while (true) {
                        PickTask task = queue.take(); // (3)!
                        if (task == POISON_PILL) {
                            return null;
                        }
                        Thread.sleep(5); // walking the aisles
                        itemsPicked.addAndGet(task.items());
                        pickedBy.merge(picker, 1, Integer::sum);
                    }
                });
            }

            for (int i = 1; i <= 30; i++) { // the producer: order intake
                queue.put(new PickTask("ORD-" + i, i % 4 + 1)); // blocks when 5 tasks are waiting
                maxDepth.accumulateAndGet(queue.size(), Math::max);
            }
            for (int p = 0; p < pickers; p++) {
                queue.put(POISON_PILL); // one pill per consumer
            }
        } // waits for all pickers to finish

        int orders = pickedBy.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Orders picked       : " + orders);
        System.out.println("Items picked        : " + itemsPicked.get());
        System.out.println("Queue never above 5 : " + (maxDepth.get() <= 5));
        System.out.println("All pickers worked  : " + (pickedBy.size() == pickers));
    }
}
