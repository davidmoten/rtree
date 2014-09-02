package com.github.davidmoten.rtree;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class BenchmarksRTree {

	private final RTree<Object> tree = RTreeTest.createRandomRTree(10000);

	@Benchmark
	public void createRTreeAndInsertOneEntryInto10000Entries() {
		tree.add(new Object(), RTreeTest.random());
	}

	@Benchmark
	public void searchRTreeOf10000EntriesForOneRandomEntry() {
		tree.search(RTreeTest.random()).count().toBlocking().single();
	}

}
