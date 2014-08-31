package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * The heuristic used on insert to select which node to add an Entry to.
 * 
 */
public interface Selector {

    <T> Node<T> select(Geometry r, List<? extends Node<T>> nodes);

}
