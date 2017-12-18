package com.github.davidmoten.rtree.internal.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObjectsHelperTest {

    @Test
    public void testAsClassIsAbsentIfNull() {
        assertFalse(ObjectsHelper.asClass(null, Integer.class).isPresent());
    }

    @Test
    public void testAsClassIsAbsentIfDifferentClass() {
        assertFalse(ObjectsHelper.asClass(1, String.class).isPresent());
    }

    @Test
    public void testAsClassIsPresentIfSameTypeAndNotNull() {
        assertTrue(ObjectsHelper.asClass(1, Integer.class).isPresent());
    }

    @Test
    public void coverPrivateConstructor() {
        ObjectsHelper.instantiateForTestCoveragePurposesOnly();
    }
}
