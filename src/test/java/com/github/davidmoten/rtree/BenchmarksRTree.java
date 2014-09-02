package com.github.davidmoten.rtree;

import org.openjdk.jmh.annotations.Benchmark;

public class BenchmarksRTree {

	private final RTree<Object> tree = RTreeTest.createRandomRTree(10000);

	@Benchmark
	public void createRTreeAndInsert10000Entries() {
		RTreeTest.createRandomRTree(10000);
	}

	@Benchmark
	public void searchRTreeOf10000EntriesForOneRandomEntry() {
		tree.search(RTreeTest.random()).count().toBlocking().single();
	}

}
