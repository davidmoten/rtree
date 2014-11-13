package com.github.davidmoten.rtree.geometry;

import org.junit.Test;

import com.github.davidmoten.util.TestingUtil;

public class GeometriesTest {

    @Test
    public void testPrivateConstructorForCoverageOnly() {
        TestingUtil.callConstructorAndCheckIsPrivate(Geometries.class);
    }

}
