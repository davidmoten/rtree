package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.geometry.HasMbr;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.util.ListPair;
import com.github.davidmoten.util.Pair;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class QuadraticSplitter implements Splitter {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends HasMbr> ListPair<T> split(List<T> entries) {
		Preconditions.checkArgument(entries.size() >= 2);

		// according to
		// http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node

		// find the worst combination pairwise in the list and use them to start
		// the two groups
		final Pair<T> worstCombination = worstCombination(entries);

		// worst combination to have in the same node is now e1,e2.

		// establish a group around e1 and another group around e2
		final List<T> group1 = Lists.newArrayList(worstCombination.value1());
		final List<T> group2 = Lists.newArrayList(worstCombination.value2());

		final List<T> remaining = new ArrayList<T>(entries);
		remaining.remove(worstCombination.value1());
		remaining.remove(worstCombination.value2());

		final int minGroupSize = entries.size() / 2;

		// now add the remainder to the groups using least mbr area increase
		// except in the case where minimumSize would be contradicted
		while (remaining.size() > 0) {
			assignRemaining(group1, group2, remaining, minGroupSize);
		}
		return new ListPair<T>(group1, group2);
	}

	private <T extends HasMbr> void assignRemaining(final List<T> group1,
			final List<T> group2, final List<T> remaining,
			final int minGroupSize) {
		final Rectangle mbr1 = Util.mbr(group1);
		final Rectangle mbr2 = Util.mbr(group2);
		final T item1 = getBestCandidateForGroup(remaining, group1, mbr1);
		final T item2 = getBestCandidateForGroup(remaining, group2, mbr2);
		final boolean area1LessThanArea2 = item1.mbr().add(mbr1).area() <= item2
				.mbr().add(mbr2).area();

		if (area1LessThanArea2
				&& (group2.size() + remaining.size() - 1 >= minGroupSize)
				|| !area1LessThanArea2
				&& (group1.size() + remaining.size() == minGroupSize)) {
			group1.add(item1);
			remaining.remove(item1);
		} else {
			group2.add(item2);
			remaining.remove(item2);
		}
	}

	@VisibleForTesting
	static <T extends HasMbr> T getBestCandidateForGroup(List<T> list,
			List<T> group, Rectangle groupMbr) {
		Preconditions.checkArgument(!list.isEmpty());
		Optional<T> minEntry = absent();
		Optional<Double> minArea = absent();
		for (final T entry : list) {
			final double area = groupMbr.add(entry.mbr()).area();
			if (!minArea.isPresent() || area < minArea.get()) {
				minArea = of(area);
				minEntry = of(entry);
			}
		}
		return minEntry.get();
	}

	@VisibleForTesting
	static <T extends HasMbr> Pair<T> worstCombination(List<T> entries) {
		Optional<T> e1 = absent();
		Optional<T> e2 = absent();
		{
			Optional<Double> maxArea = absent();
			for (final T entry1 : entries) {
				for (final T entry2 : entries) {
					if (entry1 != entry2) {
						final double area = entry1.mbr().add(entry2.mbr())
								.area();
						if (!maxArea.isPresent() || area > maxArea.get()) {
							e1 = of(entry1);
							e2 = of(entry2);
							maxArea = of(area);
						}
					}
				}
			}
		}
		return new Pair<T>(e1.get(), e2.get());
	}
}
