package com.github.davidmoten.rtree.geometry;

import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

public final class LineTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testDoesIntersectOtherLine() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Line b = Geometries.line(0, -1, 0, 1);
        assertTrue(Intersects.lineIntersectsLine.call(a, b));
    }

    @Test
    public void testDoesNotIntersectLine() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Line b = Geometries.line(1.1, -1, 1.1, 1);
        assertFalse(Intersects.lineIntersectsLine.call(a, b));
    }

    @Test
    public void testDoesIntersectRectangle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(0.2, -0.5, 0.8, 0.5);
        assertTrue(Intersects.lineIntersectsRectangle.call(a, b));
    }

    @Test
    public void testDoesNotIntersectRectangle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Rectangle b = Geometries.rectangle(1.2, -0.5, 1.8, 0.5);
        assertFalse(Intersects.lineIntersectsRectangle.call(a, b));
    }

    @Test
    public void testLineIntersectsCircle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 0.5, 1);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircle() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 0.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleEast() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(1.5, 0, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleEast() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(1.5, 0, 0.6);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleWest() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(-1.5, 0, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleWest() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(-1.5, 0, 0.6);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleNorth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesNotIntersectCircleSouth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.4);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDoesIntersectCircleSouth() {
        Line a = Geometries.line(-1, 0, 1, 0);
        Circle c = Geometries.circle(0, 1.5, 0.6);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
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
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineIsPointWithinCircle() {
        Line a = Geometries.line(1, 2, 1, 2);
        Circle c = Geometries.circle(1, 3, 2);
        assertTrue(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineIsPointOutsideCircle() {
        Line a = Geometries.line(1, 10, 1, 10);
        Circle c = Geometries.circle(1, 3, 2);
        assertFalse(Intersects.lineIntersectsCircle.call(a, c));
    }

    @Test
    public void testLineDistanceToRectangle() {
        Line a = Geometries.line(1, 2, 1, 2);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(Math.sqrt(5), a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenOneEndIsInside() {
        Line a = Geometries.line(1, 2, 4, 4);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenOtherEndIsInside() {
        Line a = Geometries.line(4, 4, 1, 2);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsWestEdge() {
        Line a = Geometries.line(3, 1, 3, 10);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsNorthEdge() {
        Line a = Geometries.line(2, 7, 10, 7);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsSouthEdge() {
        Line a = Geometries.line(2, 3, 10, 3);
        Rectangle r = Geometries.rectangle(3, 3, 7, 7);
        assertEquals(0, a.distance(r), PRECISION);
    }

    @Test
    public void testLineDistanceToRectangleIsZeroWhenContainsEastEdge() {
        Line a = Geometries.line(7, 1, 7, 10);
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
        Rectangle2D d;
    }
    
    @Test
    public void testLineDoubleIntersectsWithHorizontalLine() {
        {  // test expectation on Line2D.Double.intersectsLine which we depend on 
            Line2D.Double line = new Line2D.Double(35.0d, -25.0d, 45.0d, -25.0d);
            assertTrue(line.intersectsLine(40.0d, -20.0d, 45.0d, -40.0d));
        }
        {
            Line line = Geometries.line(40.0d, -20.0d, 45.0d, -40.0d);
            Line horizontalLine = Geometries.line(35.0d, -25.0d, 45.0d, -25.0d);
            
            assertTrue(line.intersects(horizontalLine.mbr()));
        }
    }
    
    @Test
    public void testLineFloatIntersectsWithHorizontalLine() {
        {  // test expectation on Line2D.Double.intersectsLine which we depend on 
            Line2D.Float line = new Line2D.Float(35.0f, -25.0f, 45.0f, -25.0f);
            assertTrue(line.intersectsLine(40.0f, -20.0f, 45.0f, -40.0f));
        }
        {
            Line line = Geometries.line(40.0f, -20.0f, 45.0f, -40.0f);
            Line horizontalLine = Geometries.line(35.0f, -25.0f, 45.0f, -25.0f);
            
            assertTrue(line.intersects(horizontalLine.mbr()));
        }
    }

}
