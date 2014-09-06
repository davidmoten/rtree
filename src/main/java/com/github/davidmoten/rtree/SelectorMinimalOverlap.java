package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Comparators.areaComparator;
import static com.github.davidmoten.rtree.Comparators.areaIncreaseComparator;
import static com.github.davidmoten.rtree.Comparators.compose;
import static com.github.davidmoten.rtree.Comparators.overlapComparator;
import static java.util.Collections.min;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class SelectorMinimalOverlap implements Selector {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
		return min(
				nodes,
				compose(overlapComparator(g.mbr(), nodes),
						areaIncreaseComparator(g.mbr()), areaComparator));
	}

}
