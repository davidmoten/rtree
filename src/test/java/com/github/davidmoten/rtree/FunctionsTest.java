package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class FunctionsTest {

    @Test
    public void testConstructorIsPrivate() {
        Asserts.assertIsUtilityClass(Functions.class);
    }
}
