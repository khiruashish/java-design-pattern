package com.handbook.microservices.competingconsumers.groups;

public class ConsumerGroupDemo {

    public static void main(String[] args) {
        System.out.println("=== Ride-hailing: 'trip-events' (6 partitions) and a Scaling Consumer Group ===");
        ConsumerGroup group = new ConsumerGroup(6);
        System.out.println("pod-1 joins -> " + group.join("pod-1"));
        System.out.println("pod-2 joins -> " + group.join("pod-2"));
        System.out.println("pod-3 joins -> " + group.join("pod-3"));
        group.join("pod-4");
        group.join("pod-5");
        group.join("pod-6");
        System.out.println("pod-7 joins -> " + group.join("pod-7") + "  <- more consumers than partitions: one idles");
        System.out.println("pod-2 dies  -> " + group.leave("pod-2"));
        System.out.println("Trip TRIP-88 always goes to partition " + ConsumerGroup.partitionFor("TRIP-88", 6)
                + ", so its events are processed in order by whoever owns it.");
    }
}
