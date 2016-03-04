package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public interface NonLeaf<T, S extends Geometry> extends Node<T, S> {

    Node<T, S> child(int i);

    /**
     * Returns a list of children nodes. For accessing individual children the
     * child(int) method should be used to ensure good performance. To avoid
     * copying an existing list though this method can be used.
     * 
     * @return list of children nodes
     */
    List<Node<T, S>> children();

}