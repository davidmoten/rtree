package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.RTreeTest.e;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;
import com.github.davidmoten.util.TestingUtil;

public class BackpressureTest {

    @Test
    public void testConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Backpressure.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBackpressureSearch() {
        Subscriber<Object> sub = Mockito.mock(Subscriber.class);
        ImmutableStack<NodePosition<Object>> stack = ImmutableStack.empty();
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        Backpressure.search(condition, sub, stack, 1);
        Mockito.verify(sub, Mockito.never()).onNext(Mockito.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestZero() {
        Subscriber<Object> sub = new Subscriber<Object>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object t) {

            }
        };
        sub.add(new Subscription() {
            volatile boolean subscribed = true;

            @Override
            public void unsubscribe() {
                subscribed = false;
            }

            @Override
            public boolean isUnsubscribed() {
                return !subscribed;
            }
        });
        Node<Object> node = Mockito.mock(Node.class);
        NodePosition<Object> np = new NodePosition<Object>(node, 1);
        ImmutableStack<NodePosition<Object>> stack = ImmutableStack.<NodePosition<Object>> empty()
                .push(np);
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        ImmutableStack<NodePosition<Object>> stack2 = Backpressure.search(condition, sub, stack, 0);
        assertTrue(stack2 == stack);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRequestZeroWhenUnsubscribed() {
        Subscriber<Object> sub = new Subscriber<Object>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Object t) {

            }
        };
        sub.add(new Subscription() {

            volatile boolean subscribed = true;

            @Override
            public void unsubscribe() {
                subscribed = false;
            }

            @Override
            public boolean isUnsubscribed() {
                return !subscribed;
            }
        });
        sub.unsubscribe();
        Node<Object> node = Mockito.mock(Node.class);
        NodePosition<Object> np = new NodePosition<Object>(node, 1);
        ImmutableStack<NodePosition<Object>> stack = ImmutableStack.<NodePosition<Object>> empty()
                .push(np);
        Func1<Geometry, Boolean> condition = Mockito.mock(Func1.class);
        ImmutableStack<NodePosition<Object>> stack2 = Backpressure.search(condition, sub, stack, 1);
        assertTrue(stack2.isEmpty());
    }

    @Test
    public void testBackpressureIterateWhenNodeHasMaxChildrenAndIsRoot() {
        Entry<Object> e1 = RTreeTest.e(1);
        @SuppressWarnings("unchecked")
        List<Entry<Object>> list = Arrays.asList(e1, e1, e1, e1);
        RTree<Object> tree = RTree.star().maxChildren(4).create().add(list);
        HashSet<Entry<Object>> expected = new HashSet<Entry<Object>>(list);
        final HashSet<Entry<Object>> found = new HashSet<Entry<Object>>();
        tree.entries().subscribe(new Subscriber<Entry<Object>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object> t) {
                found.add(t);
                request(1);
            }
        });
        assertEquals(expected, found);
    }
    
    @Test
    public void testBackpressureRequestZero() {
        Entry<Object> e1 = RTreeTest.e(1);
        @SuppressWarnings("unchecked")
        List<Entry<Object>> list = Arrays.asList(e1, e1, e1, e1);
        RTree<Object> tree = RTree.star().maxChildren(4).create().add(list);
        HashSet<Entry<Object>> expected = new HashSet<Entry<Object>>(list);
        final HashSet<Entry<Object>> found = new HashSet<Entry<Object>>();
        tree.entries().subscribe(new Subscriber<Entry<Object>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object> t) {
                found.add(t);
                request(0);
            }
        });
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureIterateWhenNodeHasMaxChildrenAndIsNotRoot() {
        Entry<Object> e1 = RTreeTest.e(1);
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        for (int i = 1; i <= 17; i++)
            list.add(e1);
        RTree<Object> tree = RTree.star().maxChildren(4).create().add(list);
        HashSet<Entry<Object>> expected = new HashSet<Entry<Object>>(list);
        final HashSet<Entry<Object>> found = new HashSet<Entry<Object>>();
        tree.entries().subscribe(new Subscriber<Entry<Object>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object> t) {
                found.add(t);
                request(1);
            }
        });
        assertEquals(expected, found);
    }

    @Test
    public void testBackpressureIterateWhenConditionFailsAgainstNonLeafNode() {
        Entry<Object> e1 = e(1);
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        for (int i = 1; i <= 17; i++)
            list.add(e1);
        list.add(e(2));
        RTree<Object> tree = RTree.star().maxChildren(4).create().add(list);
        HashSet<Entry<Object>> expected = new HashSet<Entry<Object>>(list);
        final HashSet<Entry<Object>> found = new HashSet<Entry<Object>>();
        tree.entries().subscribe(new Subscriber<Entry<Object>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Entry<Object> t) {
                found.add(t);
                request(1);
            }
        });
        assertEquals(expected, found);
    }

}
