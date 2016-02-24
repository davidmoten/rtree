package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

interface Leaf<T, S extends Geometry> extends Node<T, S> {

    List<Entry<T, S>> entries();

}