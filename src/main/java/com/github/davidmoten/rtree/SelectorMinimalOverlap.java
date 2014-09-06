package com.github.davidmoten.rtree;

import java.util.Collections;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class SelectorMinimalOverlap implements Selector {

	@Override
	public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
		return findMinimalOverlap(g.mbr(), nodes);
	}

	private static <T> Node<T> findMinimalOverlap(Rectangle r,
			List<? extends Node<T>> nodes) {
		return Collections.min(nodes, Comparators.overlapComparator(r, nodes));
	}

}
