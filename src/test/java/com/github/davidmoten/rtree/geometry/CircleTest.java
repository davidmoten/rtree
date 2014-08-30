package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;

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
}
