package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

public interface NodeFactory<T, S extends Geometry>
        extends LeafFactory<T, S>, NonLeafFactory<T, S> {
}
