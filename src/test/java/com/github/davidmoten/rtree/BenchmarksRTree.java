package com.github.davidmoten.rtree;

import org.openjdk.jmh.annotations.Benchmark;

public class BenchmarksRTree {

	@Benchmark
	public void inserts() {
		RTreeTest.createRandomRTree(10000);
	}

}
