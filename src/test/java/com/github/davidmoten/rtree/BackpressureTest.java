package com.github.davidmoten.rtree;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

public class BackpressureTest {

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
        ImmutableStack<NodePosition<Object>> stack2 = Backpressure.search(condition, sub, stack,1);
        assertTrue(stack2.isEmpty());
    }

    
}
