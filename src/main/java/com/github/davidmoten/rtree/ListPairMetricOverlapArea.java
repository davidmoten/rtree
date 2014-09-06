package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;

public class ListPairMetricOverlapArea implements ListPairMetric {

	@Override
	public Double call(ListPair<? extends HasGeometry> pair) {
		return (double) pair.group1().geometry().mbr()
				.intersectionArea(pair.group2().geometry().mbr());
	}

}
