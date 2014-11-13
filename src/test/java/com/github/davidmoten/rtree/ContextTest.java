package com.github.davidmoten.rtree;

import org.junit.Test;

public class ContextTest {

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
        new Context(0, 4, new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
    
    @Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
        new Context(1, 2, new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
        new Context(4, 3, new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }

    @Test
    public void testContextLegalChildren() {
        new Context(2, 4, new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSelectorNullThrowsNPE() {
        new Context(2, 4, null, new SplitterQuadratic());
    }
    
    @Test(expected = NullPointerException.class)
    public void testContextSplitterNullThrowsNPE() {
        new Context(2, 4, new SelectorMinimalAreaIncrease(), null);
    }
}
