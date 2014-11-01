package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Comparators.areaComparator;
import static com.github.davidmoten.rtree.Comparators.areaIncreaseComparator;
import static com.github.davidmoten.rtree.Comparators.compose;
import static com.github.davidmoten.rtree.Comparators.overlapAreaComparator;
import static java.util.Collections.min;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class SelectorMinimalOverlapArea implements Selector {

    @SuppressWarnings("unchecked")
    @Override
    public <T, S extends Geometry> Node<T, S> select(Geometry g, List<? extends Node<T, S>> nodes) {
        return min(
                nodes,
                compose(overlapAreaComparator(g.mbr(), nodes), areaIncreaseComparator(g.mbr()),
                        areaComparator(g.mbr())));
    }

}
