package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.List;

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
		List<Node<T>> best = new ArrayList<Node<T>>();
		Optional<Double> bestMetric = Optional.absent();
		for (Node<T> node : list) {
			double m = node.geometry().mbr().add(r).area()
					- node.geometry().mbr().area();
			if (!bestMetric.isPresent() || m < bestMetric.get()) {
				bestMetric = of(m);
				best = new ArrayList<Node<T>>();
				best.add(node);
			} else if (bestMetric.isPresent() && m == bestMetric.get()) {
				best.add(node);
			}
		}

		Optional<Node<T>> least = Optional.absent();
		Optional<Double> leastIncrease = Optional.absent();
		Optional<Double> leastArea = Optional.absent();
		for (Node<T> node : best) {
			double area = node.geometry().mbr().area();
			double areaIncrease = node.geometry().mbr().add(r).area() - area;
			if (!least.isPresent() || areaIncrease < leastIncrease.get()) {
				least = of(node);
				leastIncrease = of(areaIncrease);
				leastArea = of(area);
			} else if (least.isPresent() && areaIncrease == leastIncrease.get()
					&& area < leastArea.get()) {
				least = of(node);
				leastIncrease = of(areaIncrease);
				leastArea = of(area);
			}
		}
		return least.get();
	}

}
