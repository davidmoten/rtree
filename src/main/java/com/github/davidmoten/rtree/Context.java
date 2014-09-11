package com.github.davidmoten.rtree;

import com.google.common.base.Preconditions;

public final class Context {

    private final int maxChildren;
    private final int minChildren;
    private final Splitter splitter;
    private final Selector selector;

    public Context(int minChildren, int maxChildren, Selector selector, Splitter splitter) {
        Preconditions.checkNotNull(splitter);
        Preconditions.checkArgument(maxChildren > 2);
        Preconditions.checkArgument(minChildren >= 1);
        this.selector = selector;
        this.maxChildren = maxChildren;
        this.minChildren = minChildren;
        this.splitter = splitter;
    }

    public int maxChildren() {
        return maxChildren;
    }

    public int minChildren() {
        return minChildren;
    }

    public Splitter splitter() {
        return splitter;
    }

    public Selector selector() {
        return selector;
    }

}
