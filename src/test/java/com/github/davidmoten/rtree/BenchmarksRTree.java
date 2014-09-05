package com.github.davidmoten.rtree;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

@State(Scope.Benchmark)
public class BenchmarksRTree {

	private final RTree<Object> tree = createTree(100000);

	private final RTree<Object> starTree = RTree.maxChildren(4).star().create();

	@Benchmark
	public void defaultRTreeInsertOneEntryInto100KEntries() {
		tree.add(new Object(), RTreeTest.random());
	}

	@Benchmark
	public void defaultRTreeSearchOf100KPointsUsingSmallishRectangle() {
		tree.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto100KEntries() {
		starTree.add(new Object(), RTreeTest.random());
	}

	@Benchmark
	public void rStarTreeSearchOf100KPointsUsingSmallishRectangle() {
		starTree.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	private static final RTree<Object> createTree(long n) {
		RTree<Object> tree = RTree.maxChildren(10).create();
		for (int i = 0; i < n; i++) {
			tree = tree.add(new Object(), createPoint());
		}
		return tree;
	}

	private static final Point createPoint() {
		double x = Math.random() * 1000;
		double y = Math.random() * 1000;
		return Geometries.point(x, y);
	}

}
