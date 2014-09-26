package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.util.TestingUtil;

public class ComparatorsTest {

    @Test
    public void testConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Comparators.class);
    }

}
