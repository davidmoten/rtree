package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometries;

public class EntryTest {

    @Test
    public void testValue() {
        assertEquals(1, (int) Entry.entry(1, Geometries.point(0, 0)).value());
    }

    @Test
    public void testEquality() {
        assertEquals(Entry.entry(1, Geometries.point(0, 0)), Entry.entry(1, Geometries.point(0, 0)));
    }

    @Test
    public void testInequality() {
        assertNotEquals(Entry.entry(1, Geometries.point(0, 0)),
                Entry.entry(2, Geometries.point(0, 0)));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(Entry.entry(1, Geometries.point(0, 0)).equals(null));
    }

}
