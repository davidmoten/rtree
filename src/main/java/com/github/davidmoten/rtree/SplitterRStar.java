package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.internal.Comparators;

public final class SplitterRStar implements Splitter {

    private final Comparator<ListPair<?>> comparator;

    @SuppressWarnings("unchecked")
    public SplitterRStar() {
        this.comparator = Comparators.compose(Comparators.overlapListPairComparator,
                Comparators.areaPairComparator);
    }

    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(!items.isEmpty());
        // sort nodes into increasing x, calculate min overlap where both groups
        // have more than minChildren

        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap

        List<ListPair<T>> pairs = null;
        float lowestMarginSum = Float.MAX_VALUE;
        for (SortType sortType : SortType.values()) {
            List<ListPair<T>> p = getPairs(minSize, sort(items, comparator(sortType)));
            float marginSum = marginValueSum(p);
            if (marginSum < lowestMarginSum) {
                lowestMarginSum = marginSum;
                pairs = p;
            }
        }
        return Collections.min(pairs, comparator);
    }

    private static Comparator<HasGeometry> comparator(SortType sortType) {
        switch (sortType) {
        case X_LOWER:
            return INCREASING_X_LOWER;
        case X_UPPER:
            return INCREASING_X_UPPER;
        case Y_LOWER:
            return INCREASING_Y_LOWER;
        case Y_UPPER:
            return INCREASING_Y_UPPER;
        default:
            throw new IllegalArgumentException("unknown SortType " + sortType);
        }
    }

    private static enum SortType {
        X_LOWER, X_UPPER, Y_LOWER, Y_UPPER;
    }

    private static <T extends HasGeometry> float marginValueSum(List<ListPair<T>> list) {
        float sum = 0;
        for (ListPair<T> p : list)
            sum += p.marginSum();
        return sum;
    }

    @VisibleForTesting
    static <T extends HasGeometry> List<ListPair<T>> getPairs(int minSize, List<T> list) {
        List<ListPair<T>> pairs = new ArrayList<ListPair<T>>(list.size() - 2 * minSize + 1);
        for (int i = minSize; i < list.size() - minSize + 1; i++) {
            // Note that subList returns a view of list so creating list1 and
            // list2 doesn't
            // necessarily incur array allocation costs.
            List<T> list1 = list.subList(0, i);
            List<T> list2 = list.subList(i, list.size());
            ListPair<T> pair = new ListPair<T>(list1, list2);
            pairs.add(pair);
        }
        return pairs;
    }

    private static <T extends HasGeometry> List<T> sort(List<T> items,
            Comparator<HasGeometry> comparator) {
        ArrayList<T> list = new ArrayList<T>(items);
        Collections.sort(list, comparator);
        return list;
    }

    private static final Comparator<HasGeometry> INCREASING_X_LOWER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return Float.compare(n1.geometry().mbr().x1(), n2.geometry().mbr().x1());
        }
    };

    private static final Comparator<HasGeometry> INCREASING_X_UPPER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return Float.compare(n1.geometry().mbr().x2(), n2.geometry().mbr().x2());
        }
    };

    private static final Comparator<HasGeometry> INCREASING_Y_LOWER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return Float.compare(n1.geometry().mbr().y1(), n2.geometry().mbr().y1());
        }
    };

    private static final Comparator<HasGeometry> INCREASING_Y_UPPER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return Float.compare(n1.geometry().mbr().y2(), n2.geometry().mbr().y2());
        }
    };

}
