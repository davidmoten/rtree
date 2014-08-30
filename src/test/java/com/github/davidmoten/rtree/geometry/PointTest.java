package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class PointTest {

	private static final double PRECISION = 0.000001;

	@Test
	public void testCoordinates() {
		Point point = Geometries.point(1, 2);
		assertEquals(1, point.x(), PRECISION);
		assertEquals(2, point.y(), PRECISION);
	}

	@Test
	public void testDistance() {
		Point p1 = Geometries.point(1, 2);
		Rectangle r = Geometries.rectangle(4, 6, 4, 6);
		assertEquals(5, p1.distance(r), PRECISION);
	}

	@Test
	public void testMbr() {
		Point p = Geometries.point(1, 2);
		Rectangle r = Geometries.rectangle(1, 2, 1, 2);
		assertEquals(r, p.mbr());
	}
}
