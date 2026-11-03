package com.handbook.microservices.leaderelection.sequential;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/** ZooKeeper recipe: ephemeral sequential znodes; lowest sequence leads; each node watches its predecessor only. */
public final class SequentialElection {

    private final TreeMap<Integer, String> znodes = new TreeMap<>(); // seq -> member
    private int nextSeq;
    private final List<String> notifications = new ArrayList<>();

    public int join(String member) {
        int seq = nextSeq++;
        znodes.put(seq, member); // (1)!
        return seq;
    }

    /** Session ends (crash or close): its ephemeral znode disappears and only its watcher is notified. */
    public void sessionExpired(String member) {
        Integer seq = znodes.entrySet().stream().filter(e -> e.getValue().equals(member)).map(Map.Entry::getKey).findFirst().orElseThrow();
        Map.Entry<Integer, String> watcher = znodes.higherEntry(seq); // (2)!
        znodes.remove(seq);
        if (watcher != null) {
            boolean nowLeader = znodes.firstKey().equals(watcher.getKey());
            notifications.add(watcher.getValue() + " notified (predecessor " + member + " gone) -> "
                    + (nowLeader ? "now LEADER" : "watches " + znodes.lowerEntry(watcher.getKey()).getValue()));
        }
    }

    public Optional<String> leader() {
        return znodes.isEmpty() ? Optional.empty() : Optional.of(znodes.firstEntry().getValue());
    }

    public List<String> drainNotifications() {
        List<String> out = List.copyOf(notifications);
        notifications.clear();
        return out;
    }
}
