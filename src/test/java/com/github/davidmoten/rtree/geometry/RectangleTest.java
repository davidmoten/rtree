package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RectangleTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDistanceToSelfIsZero() {
        Rectangle r = rectangle(0, 0, 1, 1);
        assertEquals(0, r.distance(r), PRECISION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testXParametersWrongOrderThrowsException() {
        rectangle(2, 0, 1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testYParametersWrongOrderThrowsException() {
        rectangle(0, 2, 1, 1);
    }

    @Test
    public void testDistanceToOverlapIsZero() {
        Rectangle r = rectangle(0, 0, 2, 2);
        Rectangle r2 = rectangle(1, 1, 3, 3);

        assertEquals(0, r.distance(r2), PRECISION);
        assertEquals(0, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnly() {
        Rectangle r = rectangle(0, 0, 2, 2);
        Rectangle r2 = rectangle(3, 0, 4, 2);

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByXOnlyAndOverlapOnY() {
        Rectangle r = rectangle(0, 0, 2, 2);
        Rectangle r2 = rectangle(3, 1.5f, 4, 3.5f);

        assertEquals(1, r.distance(r2), PRECISION);
        assertEquals(1, r2.distance(r), PRECISION);
    }

    @Test
    public void testDistanceWhenSeparatedByDiagonally() {
        Rectangle r = rectangle(0, 0, 2, 1);
        Rectangle r2 = rectangle(3, 6, 10, 8);

        assertEquals(Math.sqrt(26), r.distance(r2), PRECISION);
        assertEquals(Math.sqrt(26), r2.distance(r), PRECISION);
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(rectangle(0, 0, 1, 1).equals(null));
    }

    @Test
    public void testSimpleEquality() {
        Rectangle r = rectangle(0, 0, 2, 1);
        Rectangle r2 = rectangle(0, 0, 2, 1);

        assertTrue(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality1() {
        Rectangle r = rectangle(0, 0, 2, 1);
        Rectangle r2 = rectangle(0, 0, 2, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality2() {
        Rectangle r = rectangle(0, 0, 2, 1);
        Rectangle r2 = rectangle(1, 0, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality3() {
        Rectangle r = rectangle(0, 0, 2, 1);
        Rectangle r2 = rectangle(0, 1, 2, 1);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testSimpleInEquality4() {
        Rectangle r = rectangle(0, 0, 2, 2);
        Rectangle r2 = rectangle(0, 0, 1, 2);

        assertFalse(r.equals(r2));
    }

    @Test
    public void testGeometry() {
        Rectangle r = rectangle(0, 0, 2, 1);
        assertTrue(r.equals(r.geometry()));
    }

    @Test
    public void testIntersects() {
        Rectangle a = rectangle(14, 14, 86, 37);
        Rectangle b = rectangle(13, 23, 50, 80);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsNoRectangleContainsCornerOfAnother() {
        Rectangle a = rectangle(10, 10, 50, 50);
        Rectangle b = rectangle(28.0, 4.0, 34.0, 85.0);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testIntersectsOneRectangleContainsTheOther() {
        Rectangle a = rectangle(10, 10, 50, 50);
        Rectangle b = rectangle(20, 20, 40, 40);
        assertTrue(a.intersects(b));
        assertTrue(b.intersects(a));
    }

    @Test
    public void testContains() {
        Rectangle r = rectangle(10, 20, 30, 40);
        assertTrue(r.contains(20, 30));
    }

    @Test
    public void testContainsReturnsFalseWhenLessThanMinY() {
        Rectangle r = rectangle(10, 20, 30, 40);
        assertFalse(r.contains(20, 19));
    }

    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxY() {
        Rectangle r = rectangle(10, 20, 30, 40);
        assertFalse(r.contains(20, 41));
    }

    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxX() {
        Rectangle r = rectangle(10, 20, 30, 40);
        assertFalse(r.contains(31, 30));
    }

    @Test
    public void testContainsReturnsFalseWhenLessThanMinX() {
        Rectangle r = rectangle(10, 20, 30, 40);
        assertFalse(r.contains(9, 30));
    }

    @Test
    public void testIntersectionAreWhenEqual() {
        Rectangle a = rectangle(10, 10, 30, 20);
        Rectangle b = rectangle(10, 10, 30, 20);
        assertEquals(200f, a.intersectionArea(b), 0.0001);
    }

    @Test
    public void testIntersectionAreaWhenDontIntersect() {
        Rectangle a = rectangle(10, 10, 30, 20);
        Rectangle b = rectangle(50, 50, 60, 60);
        assertEquals(0f, a.intersectionArea(b), 0.0001);
    }

    @Test
    public void testIntersectionAreaCornerIntersect() {
        Rectangle a = rectangle(10, 10, 30, 20);
        Rectangle b = rectangle(28, 17, 40, 40);
        assertEquals(6f, a.intersectionArea(b), 0.0001);
    }

    @Test
    public void testIntersectionAreaTopIntersect() {
        Rectangle a = rectangle(10, 10, 30, 20);
        Rectangle b = rectangle(8, 17, 40, 40);
        assertEquals(60f, a.intersectionArea(b), 0.0001);
    }

}