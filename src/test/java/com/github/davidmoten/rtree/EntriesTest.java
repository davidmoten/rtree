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
        assertEquals(1, (int) Entries.entry(1, Geometries.point(new float[]{0f, 0f})).value());
    }

    @Test
    public void testEquality() {
        assertEquals(Entries.entry(1, Geometries.point(new float[]{0f, 0f})), Entries.entry(1, Geometries.point(new float[]{0f, 0f})));
    }

    @Test
    public void testEqualityWithGeometry() {
        assertNotEquals(Entries.entry(1, Geometries.point(new float[]{0f, 0f})),
                Entries.entry(1, Geometries.point(new float[]{0f, 1f})));
    }

    @Test
    public void testInequality() {
        assertNotEquals(Entries.entry(1, Geometries.point(new float[]{0f, 0f})),
                Entries.entry(2, Geometries.point(new float[]{0f, 0f})));
    }

    @Test
    public void testInequalityWithNull() {
        assertFalse(Entries.entry(1, Geometries.point(new float[]{0f, 0f})).equals(null));
    }
    
    @Test
    public void testIsUtilityClass() {
        Asserts.assertIsUtilityClass(Entries.class);
    }

}
