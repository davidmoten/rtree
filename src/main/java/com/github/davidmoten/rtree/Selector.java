package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Rectangle;

/**
 * The heuristic used on insert to select which node to add an Entry to.
 *
 * @param <T>
 */
public interface Selector {

    <T> Node<T> select(Rectangle r, List<? extends Node<T>> nodes);

}
