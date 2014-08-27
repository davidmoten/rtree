package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.util.List;

import com.google.common.base.Optional;

public class QuadraticSplitter implements Splitter {

	@Override
	public <T extends HasMbr> ListPair<T> split(List<T> entries) {

		// according to
		// http://en.wikipedia.org/wiki/R-tree#Splitting_an_overflowing_node
		Optional<HasMbr> e1 = absent();
		Optional<HasMbr> e2 = absent();
		{
			Optional<Double> maxArea = absent();
			for (final HasMbr entry1 : entries) {
				for (final HasMbr entry2 : entries) {
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

		// TODO
		return null;
	}
}
