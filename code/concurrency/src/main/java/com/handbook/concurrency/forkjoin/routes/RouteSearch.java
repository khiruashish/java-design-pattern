package com.handbook.concurrency.forkjoin.routes;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/** Fork/Join over a tree of choices: find the cheapest delivery sequence by branching. */
public final class RouteSearch extends RecursiveTask<RouteSearch.Route> {

    public record Route(List<String> stops, int km) { }

    private final int[][] distance;
    private final List<String> names;
    private final List<Integer> path;
    private final int km;

    public RouteSearch(int[][] distance, List<String> names, List<Integer> path, int km) {
        this.distance = distance;
        this.names = names;
        this.path = path;
        this.km = km;
    }

    @Override
    protected Route compute() {
        if (path.size() == names.size()) {
            return new Route(path.stream().map(names::get).toList(), km);
        }
        int last = path.get(path.size() - 1);
        List<RouteSearch> branches = new java.util.ArrayList<>();
        for (int next = 0; next < names.size(); next++) {
            if (!path.contains(next)) {
                List<Integer> p = new java.util.ArrayList<>(path);
                p.add(next);
                branches.add(new RouteSearch(distance, names, p, km + distance[last][next]));
            }
        }
        if (path.size() >= names.size() - 3) { // (1)!
            return branches.stream().map(RouteSearch::compute).reduce(RouteSearch::better).orElseThrow();
        }
        return invokeAll(branches).stream().map(RouteSearch::join).reduce(RouteSearch::better).orElseThrow(); // (2)!
    }

    static Route better(Route a, Route b) {
        if (a.km() != b.km()) {
            return a.km() < b.km() ? a : b;
        }
        return a.stops().toString().compareTo(b.stops().toString()) <= 0 ? a : b; // deterministic tie-break
    }
}
