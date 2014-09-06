package com.github.davidmoten.rtree;

import java.util.Comparator;
import java.util.List;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class Comparators {

	public static Func1<HasGeometry, Double> overlap(final Rectangle r,
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

	public static Func1<HasGeometry, Double> areaIncrease(final Rectangle r,
			HasGeometry g) {
		return new Func1<HasGeometry, Double>() {
			@Override
			public Double call(HasGeometry g) {
				Rectangle gPlusR = g.geometry().mbr().add(r);
				return (double) (gPlusR.area() - g.geometry().mbr().area());
			}
		};
	}

	public static <T extends HasGeometry> Comparator<HasGeometry> overlapComparator(
			final Rectangle r, final List<T> list) {
		final Func1<HasGeometry, Double> overlap = overlap(r, list);
		return new Comparator<HasGeometry>() {

			@Override
			public int compare(HasGeometry g1, HasGeometry g2) {
				return overlap.call(g1).compareTo(overlap.call(g2));
			}
		};
	}
}
