package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree.geometry.Geometries;

public class EntriesTest {

    @Test
    public void testValue() {
        assertEquals(1, (int) Entries.entry(1, Geometries.point(0, 0)).value());
    }

    @Test
    public void testEquality() {
        assertEquals(Entries.entry(1, Geometries.point(0, 0)), Entries.entry(1, Geometries.point(0, 0)));
    }

    @Test
    public void testEqualityWithGeometry() {
        assertNotEquals(Entries.entry(1, Geometries.point(0, 0)),
                Entries.entry(1, Geometries.point(0, 1)));
    }

    @Test
    public void testInequality() {
        assertNotEquals(Entries.entry(1, Geometries.point(0, 0)),
                Entries.entry(2, Geometries.point(0, 0)));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(Entries.entry(1, Geometries.point(0, 0)).equals(null));
    }
    
    @Test
    public void testIsUtilityClass() {
        Asserts.assertIsUtilityClass(Entries.class);
    }

}
