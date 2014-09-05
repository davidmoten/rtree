package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Optional;

public class SelectorMinimalOverlap implements Selector {

	@Override
	public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
		return findLeastMbrOverlap(g.mbr(), nodes);
	}

	private static <T> Node<T> findLeastMbrOverlap(Rectangle r,
			List<? extends Node<T>> list) {
		Optional<Double> bestMetric = Optional.absent();
		Optional<Node<T>> best = Optional.absent();
		for (Node<T> m : list) {
			double diff = m.geometry().mbr().add(r)
					.intersectionArea(m.geometry().mbr());
			if (!bestMetric.isPresent() || diff < bestMetric.get()) {
				bestMetric = Optional.of(diff);
				best = Optional.of(m);
			}
		}
		return best.get();
	}

}
