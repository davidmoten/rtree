package com.github.davidmoten.rtree;

import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.ListPair;

public interface ListPairMetric extends
		Func1<ListPair<? extends HasGeometry>, Double> {

}
