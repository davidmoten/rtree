package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.util.ListPair;

public class ListPairMetricOverlapArea implements ListPairMetric {

	@Override
	public Double call(ListPair<? extends HasGeometry> pair) {
		return (double) Util.mbr(pair.list1()).intersectionArea(
				Util.mbr(pair.list2()));
	}

}
