package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public interface LeafFactory<T, S extends Geometry> {
    Leaf<T, S> create(List<Entry<T, S>> entries, Context context);
}
