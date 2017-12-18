package com.github.davidmoten.rtree;

import static com.github.davidmoten.guavamini.Optional.absent;
import static com.github.davidmoten.guavamini.Optional.of;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.guavamini.Lists;
import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.Util;
import com.github.davidmoten.rtree.internal.util.Pair;

public final class SplitterQuadratic implements Splitter {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
        Preconditions.checkArgument(items.size() >= 2);

        // according to
        // http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node

        // find the worst combination pairwise in the list and use them to start
        // the two groups
        final Pair<T> worstCombination = worstCombination(items);

        // worst combination to have in the same node is now e1,e2.

        // establish a group around e1 and another group around e2
        final List<T> group1 = Lists.newArrayList(worstCombination.value1());
        final List<T> group2 = Lists.newArrayList(worstCombination.value2());

        final List<T> remaining = new ArrayList<T>(items);
        remaining.remove(worstCombination.value1());
        remaining.remove(worstCombination.value2());

        final int minGroupSize = items.size() / 2;

        // now add the remainder to the groups using least mbr area increase
        // except in the case where minimumSize would be contradicted
        while (remaining.size() > 0) {
            assignRemaining(group1, group2, remaining, minGroupSize);
        }
        return new ListPair<T>(group1, group2);
    }

    private <T extends HasGeometry> void assignRemaining(final List<T> group1, final List<T> group2,
            final List<T> remaining, final int minGroupSize) {
        final Rectangle mbr1 = Util.mbr(group1);
        final Rectangle mbr2 = Util.mbr(group2);
        final T item1 = getBestCandidateForGroup(remaining, group1, mbr1);
        final T item2 = getBestCandidateForGroup(remaining, group2, mbr2);
        final boolean area1LessThanArea2 = item1.geometry().mbr().add(mbr1).area() <= item2
                .geometry().mbr().add(mbr2).area();

        if (area1LessThanArea2 && (group2.size() + remaining.size() - 1 >= minGroupSize)
                || !area1LessThanArea2 && (group1.size() + remaining.size() == minGroupSize)) {
            group1.add(item1);
            remaining.remove(item1);
        } else {
            group2.add(item2);
            remaining.remove(item2);
        }
    }

    @VisibleForTesting
    static <T extends HasGeometry> T getBestCandidateForGroup(List<T> list, List<T> group,
            Rectangle groupMbr) {
        // TODO reduce allocations by not using Optional
        Optional<T> minEntry = absent();
        Optional<Double> minArea = absent();
        for (final T entry : list) {
            final double area = groupMbr.add(entry.geometry().mbr()).area();
            if (!minArea.isPresent() || area < minArea.get()) {
                minArea = of(area);
                minEntry = of(entry);
            }
        }
        return minEntry.get();
    }

    @VisibleForTesting
    static <T extends HasGeometry> Pair<T> worstCombination(List<T> items) {
        //TODO reduce allocations by not using Optional
        Optional<T> e1 = absent();
        Optional<T> e2 = absent();
        {
            Optional<Double> maxArea = absent();
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    T entry1 = items.get(i);
                    T entry2 = items.get(j);
                    final double area = entry1.geometry().mbr().add(entry2.geometry().mbr()).area();
                    if (!maxArea.isPresent() || area > maxArea.get()) {
                        e1 = of(entry1);
                        e2 = of(entry2);
                        maxArea = of(area);
                    }
                }
            }
        }
        if (e1.isPresent())
            return new Pair<T>(e1.get(), e2.get());
        else
            // all items are the same item
            return new Pair<T>(items.get(0), items.get(1));
    }
}
