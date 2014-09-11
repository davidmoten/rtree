package com.github.davidmoten.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImmutableStackTest {

    private final Object a = new Object();
    private final Object b = new Object();
    
    @Test
    public void testNewStackIsEmpty() {
        ImmutableStack<Object> s = ImmutableStack.empty();
        assertTrue(s.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void testPopOnEmptyStackThrowsException() {
        ImmutableStack.empty().pop();
    }

    @Test(expected = RuntimeException.class)
    public void testPeekOnEmptyStackThrowsException() {
        ImmutableStack.empty().peek();
    }

    @Test
    public void testStackIsEmptyAfterPushThenPop() {
        assertTrue(ImmutableStack.empty().push(new Object()).pop().isEmpty());
    }

    @Test
    public void testPeekGivesLastPushed() {
        assertEquals(b,ImmutableStack.empty().push(a).push(b).peek());
    }

    @Test
    public void testPopPeekGivesSecondLastPushed() {
        assertEquals(a,ImmutableStack.empty().push(a).push(b).pop().peek());
    }
    
}
