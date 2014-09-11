package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.google.common.base.Preconditions;

public class SplitterRStar implements Splitter {

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

        Map<SortType, List<ListPair<T>>> map = new HashMap<SortType, List<ListPair<T>>>();
        map.put(SortType.X_LOWER, getPairs(minSize, sort(items, INCREASING_X_LOWER)));
        map.put(SortType.X_UPPER, getPairs(minSize, sort(items, INCREASING_X_UPPER)));
        map.put(SortType.Y_LOWER, getPairs(minSize, sort(items, INCREASING_Y_LOWER)));
        map.put(SortType.Y_UPPER, getPairs(minSize, sort(items, INCREASING_Y_UPPER)));

        // compute S the sum of all margin-values of the lists above
        // the list with the least S is then used to find minimum overlap

        List<SortType> sortTypes = Arrays.asList(SortType.values());
        SortType leastMarginSumSortType = Collections.min(sortTypes, marginSumComparator(map));
        List<ListPair<T>> pairs = map.get(leastMarginSumSortType);

        return Collections.min(pairs, comparator);
    }

    private static enum SortType {
        X_LOWER, X_UPPER, Y_LOWER, Y_UPPER;
    }

    private static <T extends HasGeometry> Comparator<SortType> marginSumComparator(
            final Map<SortType, List<ListPair<T>>> map) {
        return Comparators.toComparator(new Func1<SortType, Double>() {
            @Override
            public Double call(SortType sortType) {
                return (double) marginValueSum(map.get(sortType));
            }
        });
    }

    private static <T extends HasGeometry> float marginValueSum(List<ListPair<T>> list) {
        float sum = 0;
        for (ListPair<T> p : list)
            sum += p.marginSum();
        return sum;
    }

    private static <T extends HasGeometry> List<ListPair<T>> getPairs(int minSize, List<T> list) {
        List<ListPair<T>> pairs = new ArrayList<ListPair<T>>();
        for (int i = minSize; i < list.size() - minSize; i++) {
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

    private static Comparator<HasGeometry> INCREASING_X_LOWER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return ((Float) n1.geometry().mbr().x1()).compareTo(n2.geometry().mbr().x1());
        }
    };

    private static Comparator<HasGeometry> INCREASING_X_UPPER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return ((Float) n1.geometry().mbr().x2()).compareTo(n2.geometry().mbr().x2());
        }
    };

    private static Comparator<HasGeometry> INCREASING_Y_LOWER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return ((Float) n1.geometry().mbr().y1()).compareTo(n2.geometry().mbr().y1());
        }
    };

    private static Comparator<HasGeometry> INCREASING_Y_UPPER = new Comparator<HasGeometry>() {

        @Override
        public int compare(HasGeometry n1, HasGeometry n2) {
            return ((Float) n1.geometry().mbr().y2()).compareTo(n2.geometry().mbr().y2());
        }
    };

}
