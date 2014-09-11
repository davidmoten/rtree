package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class SelectorRStar implements Selector {

    private static Selector overlapSelector = new SelectorMinimalOverlap();
    private static Selector areaIncreaseSelector = new SelectorMinimalAreaIncrease();

    @Override
    public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
        boolean leafNodes = nodes.get(0) instanceof Leaf;
        if (leafNodes)
            return overlapSelector.select(g, nodes);
        else
            return areaIncreaseSelector.select(g, nodes);
    }

}
