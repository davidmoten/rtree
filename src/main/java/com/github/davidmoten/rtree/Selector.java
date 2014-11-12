package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * The heuristic used on insert to select which node to add an Entry to.
 * 
 */
public interface Selector {

    /**
     * Returns the node from a list of nodes that an object with the given
     * geometry would be added to.
     * 
     * @param <T>
     *            type of value of entry in tree
     * @param <S>
     *            type of geometry of entry in tree
     * @param g
     *            geometry
     * @param nodes
     *            nodes to select from
     * @return one of the given nodes
     */
    <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes);

}
