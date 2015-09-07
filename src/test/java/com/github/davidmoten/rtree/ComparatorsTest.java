package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class ComparatorsTest {

    @Test
    public void testConstructorIsPrivate() {
        Asserts.assertIsUtilityClass(Comparators.class);
    }

}
