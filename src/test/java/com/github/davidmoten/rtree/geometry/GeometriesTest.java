package com.github.davidmoten.rtree.geometry;

import org.junit.Test;

public class GeometriesTest {

    @Test
    public void testPrivateConstructorForCoverageOnly() {
        Geometries.instantiateForTestCoveragePurposesOnly();
    }

}
