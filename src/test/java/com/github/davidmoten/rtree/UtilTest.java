package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.junit.Asserts;

public class UtilTest {

    @Test
    public void coverPrivateConstructor() {
        Asserts.assertIsUtilityClass(Util.class);
    }

}
