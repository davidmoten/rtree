package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

interface NodeLeafInterface<T, S extends Geometry> extends Node<T, S> {

    List<? extends Node<T, S>> children();

    Context context();

}