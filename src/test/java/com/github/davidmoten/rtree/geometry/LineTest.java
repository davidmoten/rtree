package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.internal.CircleDouble;
import com.github.davidmoten.rtree.geometry.internal.CircleFloat;
import com.github.davidmoten.rtree.geometry.internal.LineFloat;

public final class LineTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDoesIntersectOtherLine() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        LineFloat b = Geometries.line(0, -1, 0, 1);
        assertTrue(Intersects.lineIntersectsLine.call(a, b));
    }

    @Test
    public void testDoesNotIntersectLine() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        LineFloat b = Geometries.line(1.1, -1, 1.1, 1);
        assertFalse(Intersects.lineIntersectsLine.call(a, b));
    }

    @Test
    public void testDoesIntersectRectangle() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(0.2, -0.5, 0.8, 0.5);
        assertTrue(Intersects.lineIntersectsRectangle.call(a, b));
    }

    @Test
    public void testDoesNotIntersectRectangle() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(1.2, -0.5, 1.8, 0.5);
        assertFalse(Intersects.lineIntersectsRectangle.call(a, b));
    }

    @Test
    public void testLineIntersectsCircle() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(0, 0.5, 1);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircle() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(0, 0.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleEast() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(1.5, 0, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleEast() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(1.5, 0, 0.6);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleWest() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(-1.5, 0, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleWest() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(-1.5, 0, 0.6);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleNorth() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleSouth() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleSouth() {
        LineFloat a = Geometries.line(-1, 0, 1, 0);
        CircleDouble c = Geometries.circle(0, 1.5, 0.6);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineMbr() {
        LineFloat a = Geometries.line(-2, 3, 1, -1);
        Rectangle mbr = a.mbr();
        assertEquals(-2, mbr.x1(), PRECISION);
        assertEquals(-1, mbr.y1(), PRECISION);
        assertEquals(1, mbr.x2(), PRECISION);
        assertEquals(3, mbr.y2(), PRECISION);
    }

    @Test
    public void testLineSameXWithinCircle() {
        LineFloat a = Geometries.line(1, 2, 1, 4);
        CircleFloat c = Geometries.circle(1, 3, 2);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineIsPointWithinCircle() {
        LineFloat a = Geometries.line(1, 2, 1, 2);
        CircleFloat c = Geometries.circle(1, 3, 2);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineIsPointOutsideCircle() {
        LineFloat a = Geometries.line(1, 10, 1, 10);
        CircleFloat c = Geometries.circle(1, 3, 2);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDistanceToRectangle() {
        LineFloat a = Geometries.line(1, 2, 1, 2);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(Math.sqrt(5), a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenOneEndIsInside() {
        LineFloat a = Geometries.line(1, 2, 4, 4);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenOtherEndIsInside() {
        LineFloat a = Geometries.line(4, 4, 1, 2);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsWestEdge() {
        LineFloat a = Geometries.line(3, 1, 3, 10);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsNorthEdge() {
        LineFloat a = Geometries.line(2, 7, 10, 7);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsSouthEdge() {
        LineFloat a = Geometries.line(2, 3, 10, 3);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsEastEdge() {
        LineFloat a = Geometries.line(7, 1, 7, 10);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDoesNotIntersectsPoint() {
        assertFalse(Geometries.line(1.5, 1.5, 2.6, 2.5).intersects(point(2, 2)));
    }

    @Test
    public void testLineDoesIntersectPoint() {
        assertTrue(Geometries.line(1.5, 1.5, 2.5, 2.5).intersects(point(2, 2)));
    }

}
