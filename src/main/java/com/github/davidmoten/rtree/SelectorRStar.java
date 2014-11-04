package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Uses minimal overlap area selector for leaf nodes and minimal areea increase
 * selector for non-leaf nodes.
 */
public final class SelectorRStar implements Selector {

    private static Selector overlapAreaSelector = new SelectorMinimalOverlapArea();
    private static Selector areaIncreaseSelector = new SelectorMinimalAreaIncrease();

    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        boolean leafNodes = nodes.get(0) instanceof Leaf;
        if (leafNodes)
            return overlapAreaSelector.select(g, nodes);
        else
            return areaIncreaseSelector.select(g, nodes);
    }

}
