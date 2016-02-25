package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

public interface EntryFactory<T,S extends Geometry> {
    Entry<T,S> createEntry(T value, S geometry);
}
