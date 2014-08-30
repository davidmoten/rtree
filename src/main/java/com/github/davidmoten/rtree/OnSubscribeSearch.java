package com.github.davidmoten.rtree;

import java.util.concurrent.atomic.AtomicLong;

import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

final class OnSubscribeSearch<T> implements OnSubscribe<Entry<T>> {

    private final Node<T> node;
    private final Func1<? super Geometry, Boolean> condition;

    OnSubscribeSearch(Node<T> node, Func1<? super Geometry, Boolean> condition) {
        this.node = node;
        this.condition = condition;
    }

    @Override
    public void call(Subscriber<? super Entry<T>> subscriber) {
        subscriber.setProducer(new SearchProducer<T>(node, condition, subscriber));
    }

    private static class SearchProducer<T> implements Producer {

        private final Subscriber<? super Entry<T>> subscriber;
        private final Node<T> node;
        private final Func1<? super Geometry, Boolean> condition;
        private volatile ImmutableStack<NodePosition<T>> stack;
        private final AtomicLong requested = new AtomicLong(0);

        public SearchProducer(Node<T> node, Func1<? super Geometry, Boolean> condition,
                Subscriber<? super Entry<T>> subscriber) {
            this.node = node;
            this.condition = condition;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<T>(node, 0));
        }

        @Override
        public void request(long n) {
            if (requested.get() == Long.MAX_VALUE)
                // already started with fast path
                return;
            else if (n == Long.MAX_VALUE) {
                // fast path
                requestAll();
            } else {
                requestSome(n);
            }
        }

        private void requestAll() {
            node.search(condition, subscriber);
            if (!subscriber.isUnsubscribed())
                subscriber.onCompleted();
        }

        public void requestSome(long n) {
            // back pressure path
            // this algorithm copied roughly from
            // rxjava-core/OnSubscribeFromIterable.java
            long previousCount = requested.getAndAdd(n);
            if (previousCount == 0) {
                while (true) {
                    long r = requested.get();
                    long numToEmit = r;

                    stack = stack.peek().node().search(condition, subscriber, stack, numToEmit);
                    if (stack.isEmpty()) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onCompleted();
                    } else if (requested.addAndGet(-r) == 0)
                        return;
                }
            }
        }
    }

}
