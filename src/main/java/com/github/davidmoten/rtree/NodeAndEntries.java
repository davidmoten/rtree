package com.github.davidmoten.rtree;

import java.util.List;

import com.google.common.base.Optional;

public class NodeAndEntries<T> {

    private final Optional<? extends Node<T>> node;
    private final List<Entry<T>> entries;

    public NodeAndEntries(Optional<? extends Node<T>> node, List<Entry<T>> entries) {
        this.node = node;
        this.entries = entries;
    }

    public Optional<? extends Node<T>> node() {
        return node;
    }

    public List<Entry<T>> entries() {
        return entries;
    }

}
