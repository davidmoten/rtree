package com.github.davidmoten.rtree;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.davidmoten.rtree.geometry.Geometries;

@State(Scope.Benchmark)
public class BenchmarksRTree {

	private final List<Entry<Object>> entries = RTreeTest
			.createRandomEntries(100000);

	private final RTree<Object> defaultTreeM10 = RTree.maxChildren(10).create()
			.add(entries);

	private final RTree<Object> starTreeM10 = RTree.maxChildren(10).star()
			.create().add(entries);

	private final RTree<Object> defaultTreeM32 = RTree.maxChildren(32).create()
			.add(entries);

	private final RTree<Object> starTreeM32 = RTree.maxChildren(32).star()
			.create().add(entries);

	private final RTree<Object> defaultTreeM128 = RTree.maxChildren(128)
			.create().add(entries);

	private final RTree<Object> starTreeM128 = RTree.maxChildren(128).star()
			.create().add(entries);

	@Benchmark
	public void defaultRTreeInsertOneEntryInto100KEntriesMaxChildren10() {
		insert(defaultTreeM10);
	}

	@Benchmark
	public void defaultRTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren10() {
		search(defaultTreeM10);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto100KEntriesMaxChildren10() {
		insert(starTreeM10);
	}

	@Benchmark
	public void rStarTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren10() {
		search(starTreeM10);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto100KEntriesMaxChildren32() {
		insert(defaultTreeM32);
	}

	@Benchmark
	public void defaultRTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren32() {
		search(defaultTreeM32);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto100KEntriesMaxChildren32() {
		insert(starTreeM32);
	}

	@Benchmark
	public void rStarTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren32() {
		search(starTreeM32);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto100KEntriesMaxChildren128() {
		insert(defaultTreeM128);
	}

	@Benchmark
	public void defaultRTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren128() {
		search(defaultTreeM128);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto100KEntriesMaxChildren128() {
		insert(starTreeM128);
	}

	@Benchmark
	public void rStarTreeSearchOf100KPointsUsingSmallishRectangleMaxChildren128() {
		starTreeM128.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	private void search(RTree<Object> tree) {
		tree.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	private void insert(RTree<Object> tree) {
		tree.add(new Object(), RTreeTest.random());
	}

}
