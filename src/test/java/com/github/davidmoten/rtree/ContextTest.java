package com.github.davidmoten.rtree;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

public class ContextTest {

	@Test(expected = RuntimeException.class)
    public void testContextIllegalMinChildren() {
		new Context(0, 4,
				new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
	
	@Test(expected = RuntimeException.class)
    public void testContextIllegalMaxChildren() {
		new Context(1, 2,
				new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
	
	@Test(expected = RuntimeException.class)
    public void testContextIllegalMinMaxChildren() {
		new Context(4, 3,
				new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
	
	@Test
    public void testContextLegalChildren() {
		new Context(2, 4,
				new SelectorMinimalAreaIncrease(), new SplitterQuadratic());
    }
}
