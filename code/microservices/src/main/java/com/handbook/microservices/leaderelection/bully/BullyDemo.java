package com.handbook.microservices.leaderelection.bully;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** Coding challenge: the Bully algorithm. The highest-id live node always wins. */
public class BullyDemo {

    static final Set<Integer> ALIVE = new TreeSet<>();
    static final List<String> LOG = new ArrayList<>();

    static int elect(int starter, Set<Integer> all) {
        List<Integer> higher = all.stream().filter(id -> id > starter).toList();
        LOG.add("node " + starter + " sends ELECTION to " + higher);
        List<Integer> answered = higher.stream().filter(ALIVE::contains).toList();
        if (answered.isEmpty()) {
            LOG.add("no answer: node " + starter + " broadcasts COORDINATOR");
            return starter;
        }
        LOG.add("OK from " + answered + "; node " + starter + " backs off");
        return elect(answered.get(0), all); // the lowest higher live node continues the election
    }

    public static void main(String[] args) {
        System.out.println("=== Challenge: Bully Election Among 5 Inventory Nodes ===");
        Set<Integer> all = Set.of(1, 2, 3, 4, 5);
        ALIVE.addAll(all);
        ALIVE.remove(5); // the old coordinator crashed
        int leader = elect(2, new TreeSet<>(all)); // node 2 noticed the missing heartbeat
        LOG.forEach(l -> System.out.println("  " + l));
        System.out.println("New coordinator: node " + leader);
    }
}
