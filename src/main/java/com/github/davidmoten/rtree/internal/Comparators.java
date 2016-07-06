package com.github.davidmoten.rtree.internal;

import java.util.Comparator;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Selector;
import com.github.davidmoten.rtree.Splitter;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

/**
 * Utility functions asociated with {@link Comparator}s, especially for use with
 * {@link Selector}s and {@link Splitter}s.
 * 
 */
public final class Comparators {

    private Comparators() {
        // prevent instantiation
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> overlapAreaThenAreaIncreaseThenAreaComparator(
            final Rectangle r, final List<T> list) {
        return new Comparator<HasGeometry>() {

            @Override
            public int compare(HasGeometry g1, HasGeometry g2) {
                int value = Float.compare(overlapArea(r, list, g1), overlapArea(r, list, g2));
                if (value == 0) {
                    value = Float.compare(areaIncrease(r, g1), areaIncrease(r, g2));
                    if (value == 0) {
                        value = Float.compare(area(r, g1), area(r, g2));
                    }
                }
                return value;
            }
        };
    }

    private static float area(final Rectangle r, HasGeometry g1) {
        return g1.geometry().mbr().add(r).area();
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> areaIncreaseThenAreaComparator(
            final Rectangle r) {
        return new Comparator<HasGeometry>() {
            @Override
            public int compare(HasGeometry g1, HasGeometry g2) {
                int value = Float.compare(areaIncrease(r, g1), areaIncrease(r, g2));
                if (value == 0) {
                    value = Float.compare(area(r, g1), area(r, g2));
                }
                return value;
            }
        };
    }

    private static float overlapArea(Rectangle r, List<? extends HasGeometry> list,
            HasGeometry g) {
        Rectangle gPlusR = g.geometry().mbr().add(r);
        float m = 0;
        for (HasGeometry other : list) {
            if (other != g) {
                m += gPlusR.intersectionArea(other.geometry().mbr());
            }
        }
        return m;
    }

    private static float areaIncrease(Rectangle r, HasGeometry g) {
        Rectangle gPlusR = g.geometry().mbr().add(r);
        return gPlusR.area() - g.geometry().mbr().area();
    }

    /**
     * <p>
     * Returns a comparator that can be used to sort entries returned by search
     * methods. For example:
     * </p>
     * <p>
     * <code>search(100).toSortedList(ascendingDistance(r))</code>
     * </p>
     * 
     * @param <T>
     *            the value type
     * @param <S>
     *            the entry type
     * @param r
     *            rectangle to measure distance to
     * @return a comparator to sort by ascending distance from the rectangle
     */
    public static <T, S extends Geometry> Comparator<Entry<T, S>> ascendingDistance(
            final Rectangle r) {
        return new Comparator<Entry<T, S>>() {
            @Override
            public int compare(Entry<T, S> e1, Entry<T, S> e2) {
                return Double.compare(e1.geometry().distance(r), e2.geometry().distance(r));
            }
        };
    }

}
