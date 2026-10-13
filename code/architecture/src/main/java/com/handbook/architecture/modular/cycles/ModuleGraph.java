package com.handbook.architecture.modular.cycles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Coding challenge: modules must form a DAG. Find a dependency cycle if there is one (DFS with
 * colours); otherwise return a valid build/initialization order (dependencies first).
 */
public final class ModuleGraph {

    private final Map<String, TreeSet<String>> edges = new TreeMap<>();

    public ModuleGraph dependsOn(String module, String... dependencies) {
        edges.computeIfAbsent(module, m -> new TreeSet<>()).addAll(List.of(dependencies));
        for (String d : dependencies) {
            edges.computeIfAbsent(d, m -> new TreeSet<>());
        }
        return this;
    }

    public Optional<List<String>> findCycle() {
        Map<String, Integer> colour = new LinkedHashMap<>(); // 0 = new, 1 = on stack, 2 = done
        Deque<String> path = new ArrayDeque<>();
        for (String start : edges.keySet()) {
            List<String> cycle = dfs(start, colour, path);
            if (cycle != null) {
                return Optional.of(cycle);
            }
        }
        return Optional.empty();
    }

    private List<String> dfs(String node, Map<String, Integer> colour, Deque<String> path) {
        int c = colour.getOrDefault(node, 0);
        if (c == 2) {
            return null;
        }
        if (c == 1) { // back edge: the path from node to here is a cycle
            List<String> cycle = new ArrayList<>();
            boolean in = false;
            for (var it = path.descendingIterator(); it.hasNext(); ) {
                String n = it.next();
                in |= n.equals(node);
                if (in) {
                    cycle.add(n);
                }
            }
            cycle.add(node);
            return cycle;
        }
        colour.put(node, 1);
        path.push(node);
        for (String next : edges.get(node)) {
            List<String> cycle = dfs(next, colour, path);
            if (cycle != null) {
                return cycle;
            }
        }
        path.pop();
        colour.put(node, 2);
        return null;
    }

    /** Dependencies before dependents (post-order DFS). Call only when there is no cycle. */
    public List<String> buildOrder() {
        List<String> order = new ArrayList<>();
        Map<String, Boolean> seen = new TreeMap<>();
        for (String m : edges.keySet()) {
            visit(m, seen, order);
        }
        return order;
    }

    private void visit(String m, Map<String, Boolean> seen, List<String> order) {
        if (seen.putIfAbsent(m, true) != null) {
            return;
        }
        edges.get(m).forEach(d -> visit(d, seen, order));
        order.add(m);
    }
}
