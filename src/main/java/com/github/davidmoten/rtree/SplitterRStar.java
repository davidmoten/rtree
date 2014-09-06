package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;

public class SplitterRStar implements Splitter {

	private static Splitter splitter = new SplitterTopological(
			new ListPairMetricOverlapArea());

	@Override
	public <T extends HasGeometry> ListPair<T> split(List<T> items, int minSize) {
		return splitter.split(items, minSize);
	}

}
