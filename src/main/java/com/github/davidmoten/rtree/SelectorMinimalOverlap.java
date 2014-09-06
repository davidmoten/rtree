package com.github.davidmoten.rtree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class SelectorMinimalOverlap implements Selector {

	@Override
	public <T> Node<T> select(Geometry g, List<? extends Node<T>> nodes) {
		return findMinimalOverlap(g.mbr(), nodes);
	}

	private static <T> Node<T> findMinimalOverlap(Rectangle r,
			List<? extends Node<T>> nodes) {
		return Collections.min(nodes, overlapComparator(r, nodes));
	}

	private static Func1<HasGeometry, Double> overlap(final Rectangle r,
			final List<? extends HasGeometry> list) {
		return new Func1<HasGeometry, Double>() {

			@Override
			public Double call(HasGeometry g) {
				Rectangle gPlusR = g.geometry().mbr().add(r);
				double m = 0;
				for (HasGeometry other : list) {
					if (other != g) {
						m += gPlusR.intersectionArea(other.geometry().mbr());
					}
				}
				return m;
			}
		};
	}

	private static <T extends HasGeometry> Comparator<HasGeometry> overlapComparator(
			final Rectangle r, final List<T> list) {
		return new Comparator<HasGeometry>() {

			@Override
			public int compare(HasGeometry g1, HasGeometry g2) {
				return overlap(r, list).call(g1).compareTo(
						overlap(r, list).call(g2));
			}
		};
	}
}
