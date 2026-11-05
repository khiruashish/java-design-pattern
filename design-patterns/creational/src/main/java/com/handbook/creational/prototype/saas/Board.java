package com.handbook.creational.prototype.saas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Workspace board: columns of tasks. Deep-copied when a team uses a template. */
public class Board {

    private final String name;
    private final Map<String, List<Task>> columns = new LinkedHashMap<>();

    public Board(String name) {
        this.name = name;
    }

    public Board column(String column, Task... tasks) {
        columns.put(column, new ArrayList<>(List.of(tasks)));
        return this;
    }

    public Board addTask(String column, Task task) {
        columns.get(column).add(task);
        return this;
    }

    /** Deep copy of the mutable structure; tasks are immutable so they are shared. */
    public Board copyAs(String newName) {
        Board copy = new Board(newName);
        columns.forEach((col, tasks) -> copy.columns.put(col, new ArrayList<>(tasks))); // (1)!
        return copy;
    }

    /** The bug we are protecting against: a shallow copy shares the column lists. */
    public Board shallowCopyAs(String newName) {
        Board copy = new Board(newName);
        copy.columns.putAll(columns); // (2)!
        return copy;
    }

    public int taskCount() {
        return columns.values().stream().mapToInt(List::size).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name).append(" (").append(taskCount()).append(" tasks)");
        columns.forEach((col, tasks) -> sb.append("\n  ").append(col).append(": ")
                .append(tasks.stream().map(Task::title).toList()));
        return sb.toString();
    }
}
