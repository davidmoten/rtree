package com.github.davidmoten.rtree.internal;

import java.util.List;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Used for tracking deletions through recursive calls.
 * 
 * @param <T>
 *            value type
 * @param <S> geometry type
 */
public final class NodeAndEntries<T, S extends Geometry> {

    private final Optional<? extends Node<T, S>> node;
    private final List<Entry<T, S>> entries;
    private final int count;

    /**
     * Constructor.
     * 
     * @param node
     *            absent = whole node was deleted present = either an unchanged
     *            node because of no removal or the newly created node without
     *            the deleted entry
     * @param entries
     *            from nodes that dropped below minChildren in size and thus
     *            their entries are to be redistributed (readded to the tree)
     * @param countDeleted
     *            count of the number of entries removed
     */
    public NodeAndEntries(Optional<? extends Node<T, S>> node, List<Entry<T, S>> entries,
            int countDeleted) {
        this.node = node;
        this.entries = entries;
        this.count = countDeleted;
    }

    public Optional<? extends Node<T, S>> node() {
        return node;
    }

    public List<Entry<T, S>> entriesToAdd() {
        return entries;
    }

    public int countDeleted() {
        return count;
    }

}
