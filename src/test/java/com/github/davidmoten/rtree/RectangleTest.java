package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Rectangle;

public class RectangleTest {

	private static final double PRECISION = 0.00001;

	@Test
	public void testDistanceToSelfIsZero() {
		Rectangle r = new Rectangle(0, 0, 1, 1);
		assertEquals(0, r.distance(r), PRECISION);
	}

	@Test
	public void testDistanceToOverlapIsZero() {
		Rectangle r = new Rectangle(0, 0, 2, 2);
		Rectangle r2 = new Rectangle(1, 1, 3, 3);

		assertEquals(0, r.distance(r2), PRECISION);
		assertEquals(0, r2.distance(r), PRECISION);
	}

	@Test
	public void testDistanceWhenSeparatedByXOnly() {
		Rectangle r = new Rectangle(0, 0, 2, 2);
		Rectangle r2 = new Rectangle(3, 0, 4, 2);

		assertEquals(1, r.distance(r2), PRECISION);
		assertEquals(1, r2.distance(r), PRECISION);
	}

	@Test
	public void testDistanceWhenSeparatedByXOnlyAndOverlapOnY() {
		Rectangle r = new Rectangle(0, 0, 2, 2);
		Rectangle r2 = new Rectangle(3, 1.5f, 4, 3.5f);

		assertEquals(1, r.distance(r2), PRECISION);
		assertEquals(1, r2.distance(r), PRECISION);
	}

	@Test
	public void testDistanceWhenSeparatedByDiagonally() {
		Rectangle r = new Rectangle(0, 0, 2, 1);
		Rectangle r2 = new Rectangle(3, 6, 10, 8);

		assertEquals(Math.sqrt(26), r.distance(r2), PRECISION);
		assertEquals(Math.sqrt(26), r2.distance(r), PRECISION);
	}
}
