package com.github.davidmoten.rtree;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.geometry.Geometry;

public class SerializerHelper {

    public static <T, S extends Geometry> RTree<T, S> create(Optional<Node<T, S>> root, int size,
            Context<T, S> context) {
        return RTree.create(root, size, context);
    }

}
