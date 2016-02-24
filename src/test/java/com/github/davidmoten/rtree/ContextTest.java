package com.github.davidmoten.rtree;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Geometry;

public class ContextTest {

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
        new Context<Object, Geometry>(0, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), NodeFactoryDefault.instance());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
        new Context<Object, Geometry>(1, 2, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), NodeFactoryDefault.instance());
    }

    @Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
        new Context<Object, Geometry>(4, 3, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), NodeFactoryDefault.instance());
    }

    @Test
    public void testContextLegalChildren() {
        new Context<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), NodeFactoryDefault.instance());
    }

    @Test(expected = NullPointerException.class)
    public void testContextSelectorNullThrowsNPE() {
        new Context<Object, Geometry>(2, 4, null, new SplitterQuadratic(),
                NodeFactoryDefault.instance());
    }

    @Test(expected = NullPointerException.class)
    public void testContextSplitterNullThrowsNPE() {
        new Context<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(), null,
                NodeFactoryDefault.instance());
    }

    @Test(expected = NullPointerException.class)
    public void testContextNodeFactoryNullThrowsNPE() {
        new Context<Object, Geometry>(2, 4, new SelectorMinimalAreaIncrease(),
                new SplitterQuadratic(), null);
    }
}
