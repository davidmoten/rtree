package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

import java.util.Optional;

public final class SerializerHelper {
    
    private SerializerHelper() {
        // prevent instantiation
    }

    public static <T, S extends Geometry> RTree<T, S> create(Optional<Node<T, S>> root, int size,
            Context<T, S> context) {
        return RTree.create(root, size, context);
    }

}
