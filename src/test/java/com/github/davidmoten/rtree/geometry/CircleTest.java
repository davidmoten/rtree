package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class CircleTest {
    private static final double PRECISION = 0.000001;

    @Test
    public void testCoordinates() {
        Circle circle = Geometries.circle(1, 2, 3);
        assertEquals(1, circle.x(), PRECISION);
        assertEquals(2, circle.y(), PRECISION);
    }

    @Test
    public void testDistance() {
        Circle circle = Geometries.circle(0, 0, 1);
        Rectangle r = Geometries.rectangle(1, 1, 2, 2);
        assertEquals(Math.sqrt(2) - 1, circle.distance(r), PRECISION);
    }

    @Test
    public void testMbr() {
        Circle circle = Geometries.circle(1, 2, 3);
        Rectangle r = Geometries.rectangle(-2, -1, 4, 5);
        assertEquals(r, circle.mbr());
    }

    @Test
    public void testEquality() {
        Circle circle1 = Geometries.circle(1, 2, 3);
        Circle circle2 = Geometries.circle(1, 2, 3);
        assertEquals(circle1, circle2);
    }

    @Test
    public void testInequalityRadius() {
        Circle circle1 = Geometries.circle(1, 2, 3);
        Circle circle2 = Geometries.circle(1, 2, 4);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityX() {
        Circle circle1 = Geometries.circle(1, 2, 3);
        Circle circle2 = Geometries.circle(2, 2, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityY() {
        Circle circle1 = Geometries.circle(1, 2, 3);
        Circle circle2 = Geometries.circle(1, 3, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityWithNull() {
        Circle circle = Geometries.circle(1, 2, 3);
        assertFalse(circle.equals(null));
    }

    @Test
    public void testHashCode() {
        Circle circle = Geometries.circle(1, 2, 3);
        assertEquals(1606448223, circle.hashCode());
    }

}
