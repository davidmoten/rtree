package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.circle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.internal.CircleDouble;
import com.github.davidmoten.rtree.geometry.internal.CircleFloat;

public class CircleTest {
    private static final double PRECISION = 0.000001;

    @Test
    public void testCoordinates() {
        Circle circle = circle(1, 2, 3);
        assertEquals(1, circle.x(), PRECISION);
        assertEquals(2, circle.y(), PRECISION);
    }

    @Test
    public void testDistance() {
        CircleFloat circle = circle(0, 0, 1);
        Rectangle r = Geometries.rectangle(1, 1, 2, 2);
        assertEquals(Math.sqrt(2) - 1, circle.distance(r), PRECISION);
    }

    @Test
    public void testMbr() {
        CircleFloat circle = circle(1, 2, 3);
        Rectangle r = Geometries.rectangle(-2, -1, 4, 5);
        assertEquals(r, circle.mbr());
    }

    @Test
    public void testEquality() {
        CircleFloat circle1 = circle(1, 2, 3);
        CircleFloat circle2 = circle(1, 2, 3);
        assertEquals(circle1, circle2);
    }

    @Test
    public void testInequalityRadius() {
        CircleFloat circle1 = circle(1, 2, 3);
        CircleFloat circle2 = circle(1, 2, 4);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityX() {
        CircleFloat circle1 = circle(1, 2, 3);
        CircleFloat circle2 = circle(2, 2, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityY() {
        CircleFloat circle1 = circle(1, 2, 3);
        CircleFloat circle2 = circle(1, 3, 3);
        assertNotEquals(circle1, circle2);
    }

    @Test
    public void testInequalityWithNull() {
        CircleFloat circle = circle(1, 2, 3);
        assertFalse(circle.equals(null));
    }

    @Test
    public void testHashCode() {
        CircleFloat circle = circle(1, 2, 3);
        assertEquals(1606448223, circle.hashCode());
    }

    @Test
    public void testDistanceIsZeroWhenIntersects() {
        CircleFloat circle = circle(0, 0, 1);
        assertTrue(circle.distance(Geometries.rectangle(0, 1, 0, 1)) == 0);
    }

    @Test
    public void testIntersects2() {
        CircleFloat circle = circle(0, 0, 1);
        assertTrue(circle.distance(Geometries.rectangle(0, 1.1, 0, 1.1)) != 0);
    }

    @Test
    public void testIntersects3() {
        CircleFloat circle = circle(0, 0, 1);
        assertTrue(circle.distance(Geometries.rectangle(1, 1, 1, 1)) != 0);
    }

    @Test
    public void testIntersectsReturnsTrue() {
        assertTrue(circle(0, 0, 1).intersects(Geometries.rectangle(0, 0, 1, 1)));
    }

    @Test
    public void testIntersectsReturnsFalse() {
        assertFalse(circle(0, 0, 1).intersects(Geometries.rectangle(10, 10, 11, 11)));
    }

    @Test
    public void testIntersects() {
        CircleFloat a = circle(0, 0, 1);
        CircleDouble b = circle(0.1, 0.1, 1);
        assertTrue(Intersects.circleIntersectsCircle.call(a, b));
    }

    @Test
    public void testDoNotIntersect() {
        CircleFloat a = circle(0, 0, 1);
        CircleFloat b = circle(100, 100, 1);
        assertFalse(Intersects.circleIntersectsCircle.call(a, b));
    }

    @Test
    public void testIntersectsPoint() {
        assertTrue(circle(0, 0, 1).intersects(Geometries.point(0, 0)));
    }

    @Test
    public void testDoesNotIntersectPoint() {
        assertFalse(circle(0, 0, 1).intersects(Geometries.point(100, 100)));
    }
}
