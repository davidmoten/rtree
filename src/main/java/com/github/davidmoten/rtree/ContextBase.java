package com.github.davidmoten.rtree;

public interface ContextBase {

    int maxChildren();

    int minChildren();

    Splitter splitter();

    Selector selector();
}
