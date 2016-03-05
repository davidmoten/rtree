package com.github.davidmoten.rtree.geometry;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class GeometriesTest {

    private static final double PRECISION = 0.0001;

    @Test
    public void testPrivateConstructorForCoverageOnly() {
        Asserts.assertIsUtilityClass(Geometries.class);
    }

    @Test
    public void testNormalizeLongitude() {
        assertEquals(0, Geometries.normalizeLongitude(0), PRECISION);
    }

    @Test
    public void testNormalizeLongitude2() {
        assertEquals(89, Geometries.normalizeLongitude(89), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3() {
        assertEquals(179, Geometries.normalizeLongitude(179), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3_1() {
        assertEquals(-180, Geometries.normalizeLongitude(180), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3_2() {
        assertEquals(-180, Geometries.normalizeLongitude(-180), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3_3() {
        assertEquals(-179, Geometries.normalizeLongitude(-179), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3_4() {
        assertEquals(179, Geometries.normalizeLongitude(-181), PRECISION);
    }

    @Test
    public void testNormalizeLongitude4() {
        assertEquals(-179, Geometries.normalizeLongitude(181), PRECISION);
    }

    @Test
    public void testNormalizeLongitude5() {
        assertEquals(-179, Geometries.normalizeLongitude(541), PRECISION);
    }

    @Test
    public void testNormalizeLongitude2Neg() {
        assertEquals(-89, Geometries.normalizeLongitude(-89), PRECISION);
    }

    @Test
    public void testNormalizeLongitude3Neg() {
        assertEquals(-179, Geometries.normalizeLongitude(-179), PRECISION);
    }

    @Test
    public void testNormalizeLongitude4Neg() {
        assertEquals(179, Geometries.normalizeLongitude(-181), PRECISION);
    }

    @Test
    public void testNormalizeLongitude5Neg() {
        assertEquals(179, Geometries.normalizeLongitude(-541), PRECISION);
    }

    @Test
    public void testRectangleLatLong() {
        Rectangle r = Geometries.rectangleGeographic(10, -10, 5, 10);
        assertEquals(10, r.x1(), PRECISION);
        assertEquals(365, r.x2(), PRECISION);
        assertEquals(-10, r.y1(), PRECISION);
        assertEquals(10, r.y2(), PRECISION);
    }

    @Test
    public void testRectangleLatLong2() {
        Rectangle r = Geometries.rectangleGeographic(5, -10, 10, 10);
        assertEquals(5, r.x1(), PRECISION);
        assertEquals(10, r.x2(), PRECISION);
    }

    @Test
    public void testPointLatLong() {
        Point point = Geometries.pointGeographic(181, 25);
        assertEquals(-179, point.x(), PRECISION);
        assertEquals(25, point.y(), PRECISION);
    }
}
