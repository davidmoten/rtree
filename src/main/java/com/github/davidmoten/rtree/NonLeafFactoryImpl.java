package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class NonLeafFactoryImpl<T, S extends Geometry> implements NonLeafFactory<T, S> {

    @Override
    public NonLeaf<T, S> create(List<? extends Node<T, S>> children, Context context) {
        return new NonLeafImpl<T, S>(children, context);
    }

}
