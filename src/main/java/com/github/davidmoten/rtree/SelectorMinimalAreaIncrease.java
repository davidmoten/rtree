package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Comparators.areaComparator;
import static com.github.davidmoten.rtree.Comparators.areaIncreaseComparator;
import static com.github.davidmoten.rtree.Comparators.compose;
import static java.util.Collections.min;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public final class SelectorMinimalAreaIncrease implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
        return min(nodes, compose(areaIncreaseComparator(g.mbr()), areaComparator(g.mbr())));
    }
}
