package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public interface NonLeafFactory<T, S extends Geometry> {

    NonLeaf<T, S> create(List<? extends Node<T, S>> children, Context context);
}
