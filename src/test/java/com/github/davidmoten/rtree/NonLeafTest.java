package com.github.davidmoten.rtree;

import java.util.Collections;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.NonLeafDefault;

public class NonLeafTest {

    @Test(expected=IllegalArgumentException.class)
    public void testNonLeafPrecondition() {
        new NonLeafDefault<Object,Geometry>(Collections.<Node<Object,Geometry>>emptyList(), null);
    }
    
}
