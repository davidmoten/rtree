package com.github.davidmoten.rtree.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import com.github.davidmoten.rtree.internal.util.ImmutableStack;

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

    @Test(expected = RuntimeException.class)
    public void testRemoveThrowsException() {
        ImmutableStack.empty().push(a).iterator().remove();
    }

    @Test
    public void testStackIsEmptyAfterPushThenPop() {
        assertTrue(ImmutableStack.empty().push(new Object()).pop().isEmpty());
    }

    @Test
    public void testPeekGivesLastPushed() {
        assertEquals(b, ImmutableStack.empty().push(a).push(b).peek());
    }

    @Test
    public void testPopPeekGivesSecondLastPushed() {
        assertEquals(a, ImmutableStack.empty().push(a).push(b).pop().peek());
    }

    @Test
    public void testIteratorWhenEmpty() {
        assertFalse(ImmutableStack.empty().iterator().hasNext());
    }

    @Test
    public void testIteratorWhenHasOneItem() {
        assertTrue(ImmutableStack.empty().push(a).iterator().hasNext());
    }

    @Test
    public void testIteratorReturnsOneItem() {
        assertEquals(a, ImmutableStack.empty().push(a).iterator().next());
    }

    @Test
    public void testIteratorReturnsLastPushedFirst() {
        assertEquals(b, ImmutableStack.empty().push(a).push(b).iterator().next());
    }

    @Test
    public void testIteratorReturnsTwoItemsInOrderOfPop() {
        Iterator<Object> it = ImmutableStack.empty().push(a).push(b).iterator();
        assertEquals(b, it.next());
        assertEquals(a, it.next());
        assertFalse(it.hasNext());
    }

}
