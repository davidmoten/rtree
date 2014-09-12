package com.github.davidmoten.rtree;

import java.util.List;

import com.google.common.base.Optional;

public class NodeAndEntries<T> {

    private final Optional<? extends Node<T>> node;
    private final List<Entry<T>> entries;
    private final int count;

    public NodeAndEntries(Optional<? extends Node<T>> node, List<Entry<T>> entries, int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    public Optional<? extends Node<T>> node() {
        return node;
    }

    public List<Entry<T>> entriesToAdd() {
        return entries;
    }

    public int countDeleted() {
        return count;
    }

}
