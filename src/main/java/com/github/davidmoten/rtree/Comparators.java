package com.github.davidmoten.rtree;

import java.util.Comparator;
import java.util.List;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.geometry.Rectangle;

/**
 * Utility functions asociated with {@link Comparator}s, especially for use with
 * {@link Selector}s and {@link Splitter}s.
 *
 */
public final class Comparators {

    public static final Comparator<ListPair<?>> overlapListPairComparator = toComparator(Functions.overlapListPair);

    /**
     * Compares the sum of the areas of two ListPairs.
     */
    public static Comparator<ListPair<?>> areaPairComparator = new Comparator<ListPair<?>>() {

        @Override
        public int compare(ListPair<?> p1, ListPair<?> p2) {
            return ((Float) p1.areaSum()).compareTo(p2.areaSum());
        }
    };

    /**
     * Returns a {@link Comparator} that is a normal Double comparator for the
     * total of the areas of overlap of the members of the list with the
     * rectangle r.
     * 
     * @param r
     *            rectangle
     * @param list
     *            geometries to compare with the rectangle
     * @return the total of the areas of overlap of the geometries in the list
     *         with the rectangle r
     */
    public static <T extends HasGeometry> Comparator<HasGeometry> overlapAreaComparator(
            final Rectangle r, final List<T> list) {
        return toComparator(Functions.overlapArea(r, list));
    }

    public static <T extends HasGeometry> Comparator<HasGeometry> areaIncreaseComparator(
            final Rectangle r) {
        return toComparator(Functions.areaIncrease(r));
    }

    public static Comparator<HasGeometry> areaComparator(final Rectangle r) {
        return new Comparator<HasGeometry>() {

            @Override
            public int compare(HasGeometry g1, HasGeometry g2) {
                return ((Float) g1.geometry().mbr().add(r).area()).compareTo(g2.geometry().mbr()
                        .add(r).area());
            }
        };
    }

    public static <R, T extends Comparable<T>> Comparator<R> toComparator(final Func1<R, T> function) {
        return new Comparator<R>() {

            @Override
            public int compare(R g1, R g2) {
                return function.call(g1).compareTo(function.call(g2));
            }
        };
    }

    public static <T> Comparator<T> compose(final Comparator<T>... comparators) {
        return new Comparator<T>() {
            @Override
            public int compare(T t1, T t2) {
                for (Comparator<T> comparator : comparators) {
                    int value = comparator.compare(t1, t2);
                    if (value != 0)
                        return value;
                }
                return 0;
            }
        };
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
     * @param r
     *            rectangle to measure distance to
     * @param <S>
     *            the entry type
     * @return a comparator to sort by ascending distance from the rectangle
     */
    public static final <S> Comparator<Entry<S>> ascendingDistance(final Rectangle r) {
        return new Comparator<Entry<S>>() {
            @Override
            public int compare(Entry<S> e1, Entry<S> e2) {
                return ((Double) e1.geometry().distance(r)).compareTo(e2.geometry().distance(r));
            }
        };
    }
    
}
