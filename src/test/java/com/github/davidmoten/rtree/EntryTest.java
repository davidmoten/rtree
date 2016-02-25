package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;

public class EntryTest {

    @Test
    public void testValue() {
        assertEquals(1, (int) EntryDefault.entry(1, Geometries.point(0, 0)).value());
    }

    @Test
    public void testEquality() {
        assertEquals(EntryDefault.entry(1, Geometries.point(0, 0)), EntryDefault.entry(1, Geometries.point(0, 0)));
    }

    @Test
    public void testEqualityWithGeometry() {
        assertNotEquals(EntryDefault.entry(1, Geometries.point(0, 0)),
                EntryDefault.entry(1, Geometries.point(0, 1)));
    }

    @Test
    public void testInequality() {
        assertNotEquals(EntryDefault.entry(1, Geometries.point(0, 0)),
                EntryDefault.entry(2, Geometries.point(0, 0)));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(EntryDefault.entry(1, Geometries.point(0, 0)).equals(null));
    }

}
