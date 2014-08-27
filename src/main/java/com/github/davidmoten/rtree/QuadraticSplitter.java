package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class QuadraticSplitter implements Splitter {

	@Override
	public <T extends HasMbr> ListPair<T> split(List<T> entries) {
		Preconditions.checkArgument(entries.size() >= 2);

		// according to
		// http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node
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
		// worst combination to have in the same node is now e1,e2.

		// establish a group around e1 and another group around e2
		final List<T> list1 = new ArrayList<T>();
		final List<T> list2 = new ArrayList<T>();

		list1.add(e1.get());
		list2.add(e2.get());

		final List<T> remaining = new ArrayList<T>(entries);
		remaining.remove(e1.get());
		remaining.remove(e2.get());

		while (remaining.size() > 0) {

		}
		// TODO
		return null;
	}
}
