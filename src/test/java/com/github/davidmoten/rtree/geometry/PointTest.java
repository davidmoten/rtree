package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PointTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testCoordinates() {
        Point point = Geometries.point(1, 2);
        assertEquals(1, point.x(), PRECISION);
        assertEquals(2, point.y(), PRECISION);
    }

    @Test
    public void testDistanceToRectangle() {
        Point p1 = Geometries.point(1, 2);
        Rectangle r = Geometries.rectangle(4, 6, 4, 6);
        assertEquals(5, p1.distance(r), PRECISION);
    }

    @Test
    public void testDistanceToPoint() {
        Point p1 = Geometries.point(1, 2);
        Point p2 = Geometries.point(4, 6);
        assertEquals(5, p1.distance(p2), PRECISION);
    }

    @Test
    public void testMbr() {
        Point p = Geometries.point(1, 2);
        Rectangle r = Geometries.rectangle(1, 2, 1, 2);
        assertEquals(r, p.mbr());
    }

    @Test
    public void testPointIntersectsItself() {
        Point p = Geometries.point(1, 2);
        assertTrue(p.distance(p.mbr()) == 0);
    }

    @Test
    public void testIntersectIsFalseWhenPointsDiffer() {
        Point p1 = Geometries.point(1, 2);
        Point p2 = Geometries.point(1, 2.000001);
        assertFalse(p1.distance(p2.mbr()) == 0);
    }

    @Test
    public void testEquality() {
        Point p1 = Geometries.point(1, 2);
        Point p2 = Geometries.point(1, 2);
        assertTrue(p1.equals(p2));
    }

    @Test
    public void testInequality() {
        Point p1 = Geometries.point(1, 2);
        Point p2 = Geometries.point(1, 3);
        assertFalse(p1.equals(p2));
    }

    @Test
    public void testInequalityToNull() {
        Point p1 = Geometries.point(1, 2);
        assertFalse(p1.equals(null));
    }

    @Test
    public void testHashCode() {
        Point p = Geometries.point(1, 2);
        assertEquals(-1056041056, p.hashCode());
    }
}
