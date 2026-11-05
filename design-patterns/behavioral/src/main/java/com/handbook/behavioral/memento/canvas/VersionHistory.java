package com.handbook.behavioral.memento.canvas;

import java.util.ArrayList;
import java.util.List;

/** Caretaker: stores snapshots, never looks inside them. */
public class VersionHistory {

    private final List<Canvas.Snapshot> versions = new ArrayList<>();

    public void keep(Canvas.Snapshot s) {
        versions.add(s);
    }

    public Canvas.Snapshot get(String label) {
        return versions.stream().filter(v -> v.label().equals(label)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No version " + label));
    }

    public List<String> labels() {
        return versions.stream().map(Canvas.Snapshot::label).toList();
    }
}
