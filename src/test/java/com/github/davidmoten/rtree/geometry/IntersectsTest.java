package com.github.davidmoten.rtree.geometry;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class IntersectsTest {

    @Test
    public void testConstructorIsPrivate() {
        Asserts.assertIsUtilityClass(Intersects.class);
    }
/*
    @Test
    public void testRectangleIntersectsCircle() {
        assertTrue(
                Intersects.rectangleIntersectsCircle.call(rectangle(0, 0, 0, 0), circle(0, 0, 1)));
    }

    @Test
    public void testRectangleDoesNotIntersectCircle() {
        assertFalse(Intersects.rectangleIntersectsCircle.call(rectangle(0, 0, 0, 0),
                circle(100, 100, 1)));
    }
*/
}
