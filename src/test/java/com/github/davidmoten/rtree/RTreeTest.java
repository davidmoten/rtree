package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class RTreeTest {

	@Test
	public void testInstantiation() {
		RTree tree = new RTree();
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
		System.out.println("nodes="
				+ tree.nodes().toList().toBlocking().single());
	}

	private static Rectangle r(int n) {
		return Rectangle.create(n, n, n + 1, n + 1);
	}

}
