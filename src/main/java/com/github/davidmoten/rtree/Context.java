package com.github.davidmoten.rtree;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Configures an RTree prior to instantiation of an {@link RTree}.
 */
public final class Context<T, S extends Geometry> implements ContextBase {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;
    private final NodeFactory<T, S> factory;

    /**
     * Constructor.
     * 
     * @param minChildren
     *            minimum number of children per node (at least 1)
     * @param maxChildren
     *            max number of children per node (minimum is 3)
     * @param selector
     *            algorithm to select search path
     * @param splitter
     *            algorithm to split the children across two new nodes
     */
    public Context(int minChildren, int maxChildren, Selector selector, Splitter splitter,
            NodeFactory<T, S> factory) {
        Preconditions.checkNotNull(splitter);
        Preconditions.checkNotNull(selector);
        Preconditions.checkArgument(maxChildren > 2);
        Preconditions.checkArgument(minChildren >= 1);
        Preconditions.checkArgument(minChildren < maxChildren);
        Preconditions.checkNotNull(factory);
        this.selector = selector;
        this.maxChildren = maxChildren;
        this.minChildren = minChildren;
        this.splitter = splitter;
        this.factory = factory;
    }

    @Override
    public int maxChildren() {
        return maxChildren;
    }

    @Override
    public int minChildren() {
        return minChildren;
    }

    @Override
    public Splitter splitter() {
        return splitter;
    }

    @Override
    public Selector selector() {
        return selector;
    }

    public NodeFactory<T, S> factory() {
        return factory;
    }

}
