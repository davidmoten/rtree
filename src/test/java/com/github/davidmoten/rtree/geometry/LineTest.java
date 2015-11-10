package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LineTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDoesIntersectOtherLine() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Line b = Geometries.line(0, -1, 0, 1);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectLine() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Line b = Geometries.line(1.1, -1, 1.1, 1);
        assertFalse(a.intersects(b));
    }

    @Test
    public void testDoesIntersectRectangle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(0.2, -0.5, 0.8, 0.5);
        assertTrue(a.intersects(b));
    }

    @Test
    public void testDoesNotIntersectRectangle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(1.2, -0.5, 1.8, 0.5);
        assertFalse(a.intersects(b));
    }

    @Test
    public void testLineIntersectsCircle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 0.5, 1);
        assertTrue(a.intersects(c));
    }

    @Test
    public void testLineDoesNotIntersectCircle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 0.5, 0.4);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDoesNotIntersectCircleEast() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(1.5, 0, 0.4);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDoesIntersectCircleEast() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(1.5, 0, 0.6);
        assertTrue(a.intersects(c));
    }

    @Test
    public void testLineDoesNotIntersectCircleWest() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(-1.5, 0, 0.4);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDoesIntersectCircleWest() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(-1.5, 0, 0.6);
        assertTrue(a.intersects(c));
    }

    @Test
    public void testLineDoesNotIntersectCircleNorth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDoesNotIntersectCircleSouth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDoesIntersectCircleSouth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.6);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineMbr() {
        Line a = Geometries.line(-2, 3, 1, -1);
        Rectangle mbr = a.mbr();
        assertEquals(-2, mbr.x1(), PRECISION);
        assertEquals(-1, mbr.y1(), PRECISION);
        assertEquals(1, mbr.x2(), PRECISION);
        assertEquals(3, mbr.y2(), PRECISION);
    }

    @Test
    public void testLineSameXWithinCircle() {
        Line a = Geometries.line(1, 2, 1, 4);
        Circle c = Geometries.circle(1, 3, 2);
        assertTrue(a.intersects(c));
    }

    @Test
    public void testLineIsPointWithinCircle() {
        Line a = Geometries.line(1, 2, 1, 2);
        Circle c = Geometries.circle(1, 3, 2);
        assertTrue(a.intersects(c));
    }

    @Test
    public void testLineIsPointOutsideCircle() {
        Line a = Geometries.line(1, 10, 1, 10);
        Circle c = Geometries.circle(1, 3, 2);
        assertFalse(a.intersects(c));
    }

    @Test
    public void testLineDistanceToRectangle() {
        Line a = Geometries.line(1, 2, 1, 2);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(Math.sqrt(5), a.distance(r), PRECISION);
    }

}
