package com.github.davidmoten.rtree;

import java.util.ArrayList;

import org.junit.Test;

public class LeafTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCannotHaveZeroChildren() {
        Context context = new Context(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic());
        new Leaf<Object>(new ArrayList<Entry<Object>>(), context);
    }

    @Test
    public void testMbr() {
        // TODO
    }

}
