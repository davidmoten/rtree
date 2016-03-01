package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.internal.Comparators.areaComparator;
import static com.github.davidmoten.rtree.internal.Comparators.areaIncreaseComparator;
import static com.github.davidmoten.rtree.internal.Comparators.compose;
import static java.util.Collections.min;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Uses minimal area increase to select a node from a list.
 *
 */
public final class SelectorMinimalAreaIncrease implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        return min(nodes, compose(areaIncreaseComparator(g.mbr()), areaComparator(g.mbr())));
    }
}
