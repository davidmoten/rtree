package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.of;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Optional;

public final class SelectorMinimalAreaIncrease implements Selector {

	@Override
	public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
		return findLeastIncreaseInMbrArea(g.mbr(), nodes);
	}

	static <T> Node<T> findLeastIncreaseInMbrArea(Rectangle r,
			List<? extends Node<T>> list) {
		TreeSet<Node<T>> best = new TreeSet<Node<T>>(area);
		Optional<Double> bestMetric = Optional.absent();
		for (Node<T> node : list) {
			double m = node.geometry().mbr().add(r).area()
					- node.geometry().mbr().area();
			if (!bestMetric.isPresent() || m < bestMetric.get()) {
				bestMetric = of(m);
				best = new TreeSet<Node<T>>(area);
				best.add(node);
			} else if (bestMetric.isPresent() && m == bestMetric.get()) {
				best.add(node);
			}
		}
		return best.first();
	}

	private static Comparator<Node<?>> area = new Comparator<Node<?>>() {

		@Override
		public int compare(Node<?> n1, Node<?> n2) {
			return ((Float) n1.geometry().mbr().area()).compareTo(n2.geometry()
					.mbr().area());
		}
	};

}
