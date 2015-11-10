package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LineTest {

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

}
