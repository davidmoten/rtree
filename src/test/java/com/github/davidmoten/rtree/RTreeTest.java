package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RTreeTest {

	private static final double PRECISION = 0.000001;

	@Test
	public void testInstantiation() {
		RTree tree = new RTree();
		assertTrue(tree.entries().isEmpty().toBlocking().single());
	}

	@Test
	public void testSearchEmptyTree() {
		RTree tree = new RTree();
		assertTrue(tree.search(r(1)).isEmpty().toBlocking().single());
	}

	@Test
	public void testSearchOnOneItem() {
		RTree tree = new RTree();
		Entry entry = new Entry(new Object(), r(1));
		tree = tree.add(entry);
		assertEquals(Arrays.asList(entry), tree.search(r(1)).toList()
				.toBlocking().single());
		System.out.println("entries="
				+ tree.entries().toList().toBlocking().single());
	}

	@Test
	public void testPerformanceAndEntriesCount() {

		long t = System.currentTimeMillis();
		RTree tree = RTree.builder().maxChildren(4).build();
		long n = 10000;
		for (int i = 0; i < n; i++) {
			Entry entry = new Entry(new Object(), random());
			tree = tree.add(entry);
		}
		long diff = System.currentTimeMillis() - t;
		System.out.println("inserts/second = " + ((double) n / diff * 1000));
		assertEquals(n, (int) tree.entries().count().toBlocking().single());

		t = System.currentTimeMillis();
		Entry entry = tree.nearest(Rectangle.create(100, 100, 101, 101))
				.first().toBlocking().single();
		diff = System.currentTimeMillis() - t;
		System.out.println("found " + entry);
		System.out
				.println("time to get nearest with " + n + " entries=" + diff);

	}

	@Test
	public void testVisualizer() {
		RTree tree = RTree.builder().maxChildren(4).build();
		int n = 100;
		for (int i = 0; i < n; i++) {
			Entry entry = new Entry(new Object(), random());
			tree = tree.add(entry);
		}
		tree.visualize(600, 600, new Rectangle(-20, -20, 1100, 1100), 5).save(
				new File("target/tree.png"), "PNG");
	}

	@Test
	public void testFurthest() {
		RTree tree = RTree.builder().maxChildren(4).build().add(e(1)).add(e(2))
				.add(e(4)).add(e(100));
		List<Entry> list = tree.furthest(r(90)).toList().toBlocking().single();
		assertEquals(1, list.get(0).mbr().x1(), PRECISION);
		assertEquals(2, list.get(1).mbr().x1(), PRECISION);
		assertEquals(4, list.get(2).mbr().x1(), PRECISION);
		assertEquals(100, list.get(3).mbr().x1(), PRECISION);
	}

	@Test
	public void testNearest() {
		RTree tree = RTree.builder().maxChildren(4).build().add(e(1)).add(e(2))
				.add(e(4)).add(e(100));
		List<Entry> list = tree.nearest(r(90)).toList().toBlocking().single();
		assertEquals(100, list.get(0).mbr().x1(), PRECISION);
		assertEquals(4, list.get(1).mbr().x1(), PRECISION);
		assertEquals(2, list.get(2).mbr().x1(), PRECISION);
		assertEquals(1, list.get(3).mbr().x1(), PRECISION);
	}

	private static Entry e(int n) {
		return new Entry(new Object(), r(n));
	}

	private static Rectangle r(int n) {
		return Rectangle.create(n, n, n + 1, n + 1);
	}

	private static Rectangle r(int n, int m) {
		return Rectangle.create(n, m, n + 1, m + 1);
	}

	private static Rectangle random() {
		return r((int) Math.round(Math.sqrt(Math.random()) * 1000),
				(int) Math.round(Math.sqrt(Math.random()) * 1000));
	}
}
