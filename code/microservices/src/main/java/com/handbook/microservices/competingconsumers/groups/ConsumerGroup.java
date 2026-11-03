package com.handbook.microservices.competingconsumers.groups;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Kafka-style consumer group: partitions are divided among members; each partition has ONE owner. */
public final class ConsumerGroup {

    private final int partitions;
    private final List<String> members = new ArrayList<>();

    public ConsumerGroup(int partitions) {
        this.partitions = partitions;
    }

    public Map<String, List<Integer>> join(String member) {
        members.add(member);
        return rebalance();
    }

    public Map<String, List<Integer>> leave(String member) {
        members.remove(member);
        return rebalance();
    }

    /** Range assignment, like Kafka's RangeAssignor. */
    private Map<String, List<Integer>> rebalance() { // (1)!
        Map<String, List<Integer>> assignment = new LinkedHashMap<>();
        members.forEach(m -> assignment.put(m, new ArrayList<>()));
        int per = partitions / members.size();
        int extra = partitions % members.size();
        int next = 0;
        for (int i = 0; i < members.size(); i++) {
            int count = per + (i < extra ? 1 : 0); // (2)!
            for (int k = 0; k < count; k++) {
                assignment.get(members.get(i)).add(next++);
            }
        }
        return assignment;
    }

    public static int partitionFor(String key, int partitions) {
        return Math.floorMod(key.hashCode(), partitions); // same key -> same partition -> same consumer, in order
    }
}
