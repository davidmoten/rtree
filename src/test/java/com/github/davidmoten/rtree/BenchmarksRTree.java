package com.github.davidmoten.rtree;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

@State(Scope.Benchmark)
public class BenchmarksRTree {

	private final RTree<Object> tree = RTreeTest.createRandomRTree(10000);

	private final RTree<Object> tree2 = createTree();

	@Benchmark
	public void createRTreeAndInsertOneEntryInto10000Entries() {
		tree.add(new Object(), RTreeTest.random());
	}

	@Benchmark
	public void searchRTreeOf10000PointsUsingSmallishRectangle() {
		tree2.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	private static final RTree<Object> createTree() {
		RTree<Object> tree = RTree.maxChildren(10).create();
		for (int i = 0; i < 10000; i++) {
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
