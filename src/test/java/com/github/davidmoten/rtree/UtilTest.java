package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.util.TestingUtil;

public class UtilTest {

    @Test
    public void coverPrivateConstructor() {
        TestingUtil.callConstructorAndCheckIsPrivate(Util.class);
    }

}
