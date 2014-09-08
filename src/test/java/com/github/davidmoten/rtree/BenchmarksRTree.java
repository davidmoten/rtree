package com.github.davidmoten.rtree;

import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import com.github.davidmoten.rtree.geometry.Geometries;

@State(Scope.Benchmark)
public class BenchmarksRTree {

	private final List<Entry<Object>> entries = GreekEarthquakes.entriesList();

	private final List<Entry<Object>> some = RTreeTest
			.createRandomEntries(1000);

	private final RTree<Object> defaultTreeM4 = RTree.maxChildren(4).create()
			.add(entries);

	private final RTree<Object> defaultTreeM10 = RTree.maxChildren(10).create()
			.add(entries);

	private final RTree<Object> starTreeM4 = RTree.maxChildren(4).star()
			.create().add(entries);

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

	private final RTree<Object> smallDefaultTreeM4 = RTree.maxChildren(4)
			.create().add(some);

	private final RTree<Object> smallDefaultTreeM10 = RTree.maxChildren(10)
			.create().add(some);

	private final RTree<Object> smallStarTreeM10 = RTree.maxChildren(10).star()
			.create().add(some);

	private final RTree<Object> smallDefaultTreeM32 = RTree.maxChildren(32)
			.create().add(some);

	private final RTree<Object> smallStarTreeM32 = RTree.maxChildren(32).star()
			.create().add(some);

	private final RTree<Object> smallDefaultTreeM128 = RTree.maxChildren(128)
			.create().add(some);

	private final RTree<Object> smallStarTreeM128 = RTree.maxChildren(128)
			.star().create().add(some);

	@Benchmark
	public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren4() {
		insert(defaultTreeM4);
	}

	@Benchmark
	public void defaultRTreeSearchOfGreekDataPointsMaxChildren4() {
		search(defaultTreeM4);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren10() {
		insert(defaultTreeM10);
	}

	@Benchmark
	public void defaultRTreeSearchOfGreekDataPointsMaxChildren10() {
		search(defaultTreeM10);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren4() {
		insert(starTreeM4);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren10() {
		insert(starTreeM10);
	}

	@Benchmark
	public void rStarTreeSearchOfGreekDataPointsMaxChildren4() {
		search(starTreeM4);
	}

	@Benchmark
	public void rStarTreeSearchOfGreekDataPointsMaxChildren10() {
		search(starTreeM10);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren32() {
		insert(defaultTreeM32);
	}

	@Benchmark
	public void defaultRTreeSearchOfGreekDataPointsMaxChildren32() {
		search(defaultTreeM32);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren32() {
		insert(starTreeM32);
	}

	@Benchmark
	public void rStarTreeSearchOfGreekDataPointsMaxChildren32() {
		search(starTreeM32);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
		insert(defaultTreeM128);
	}

	@Benchmark
	public void defaultRTreeSearchOfGreekDataPointsMaxChildren128() {
		search(defaultTreeM128);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
		insert(starTreeM128);
	}

	@Benchmark
	public void rStarTreeSearchOfGreekDataPointsMaxChildren128() {
		starTreeM128.search(Geometries.rectangle(500, 500, 510, 510)).count()
				.toBlocking().single();
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren4() {
		insert(smallDefaultTreeM4);
	}

	@Benchmark
	public void defaultRTreeSearchOf1000PointsMaxChildren4() {
		search(smallDefaultTreeM4);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren10() {
		insert(smallDefaultTreeM10);
	}

	@Benchmark
	public void defaultRTreeSearchOf1000PointsMaxChildren10() {
		search(smallDefaultTreeM10);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren10() {
		insert(smallStarTreeM10);
	}

	@Benchmark
	public void rStarTreeSearchOf1000PointsMaxChildren10() {
		search(smallStarTreeM10);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren32() {
		insert(smallDefaultTreeM32);
	}

	@Benchmark
	public void defaultRTreeSearchOf1000PointsMaxChildren32() {
		search(smallDefaultTreeM32);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren32() {
		insert(smallStarTreeM32);
	}

	@Benchmark
	public void rStarTreeSearchOf1000PointsMaxChildren32() {
		search(smallStarTreeM32);
	}

	@Benchmark
	public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128() {
		insert(smallDefaultTreeM128);
	}

	@Benchmark
	public void defaultRTreeSearchOf1000PointsMaxChildren128() {
		search(smallDefaultTreeM128);
	}

	@Benchmark
	public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren128() {
		insert(smallStarTreeM128);
	}

	@Benchmark
	public void rStarTreeSearchOf1000PointsMaxChildren128() {
		search(smallStarTreeM128);
	}

	private void search(RTree<Object> tree) {
		tree.search(Geometries.rectangle(500, 500, 510, 510)).subscribe();
	}

	private void insert(RTree<Object> tree) {
		tree.add(new Object(), RTreeTest.random());
	}

}
