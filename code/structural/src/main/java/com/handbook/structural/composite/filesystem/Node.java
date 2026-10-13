package com.handbook.structural.composite.filesystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Challenge solution: files and folders with size and a tree printout. */
public abstract sealed class Node permits Node.File, Node.Folder {

    protected final String name;

    protected Node(String name) {
        this.name = name;
    }

    public abstract long sizeBytes();

    public abstract void print(String indent, StringBuilder out);

    public static final class File extends Node {
        private final long size;

        public File(String name, long size) {
            super(name);
            this.size = size;
        }

        public long sizeBytes() { return size; }

        public void print(String indent, StringBuilder out) {
            out.append(indent).append(name).append(" (").append(size).append(" B)\n");
        }
    }

    public static final class Folder extends Node {
        private final List<Node> children = new ArrayList<>();

        public Folder(String name, Node... children) {
            super(name);
            this.children.addAll(List.of(children));
        }

        public long sizeBytes() {
            return children.stream().mapToLong(Node::sizeBytes).sum();
        }

        public void print(String indent, StringBuilder out) {
            out.append(indent).append(name).append("/ (").append(sizeBytes()).append(" B)\n");
            children.stream().sorted(Comparator.comparing(n -> n.name))
                    .forEach(c -> c.print(indent + "  ", out));
        }
    }
}
