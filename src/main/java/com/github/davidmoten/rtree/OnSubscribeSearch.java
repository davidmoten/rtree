package com.github.davidmoten.rtree;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

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

        private volatile long requested = 0;
        @SuppressWarnings("rawtypes")
        private static final AtomicLongFieldUpdater<SearchProducer> REQUESTED_UPDATER = AtomicLongFieldUpdater
                .newUpdater(SearchProducer.class, "requested");

        SearchProducer(Node<T> node, Func1<? super Geometry, Boolean> condition,
                Subscriber<? super Entry<T>> subscriber) {
            this.node = node;
            this.condition = condition;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<T>(node, 0));
        }

        @Override
        public void request(long n) {
            try {
                if (REQUESTED_UPDATER.get(this) == Long.MAX_VALUE)
                    // already started with fast path
                    return;
                else if (n == Long.MAX_VALUE) {
                    // fast path
                    requestAll();
                } else {
                    requestSome(n);
                }
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

        private void requestAll() {
            REQUESTED_UPDATER.set(this, Long.MAX_VALUE);
            node.search(condition, subscriber);
            if (!subscriber.isUnsubscribed())
                subscriber.onCompleted();
        }

        private void requestSome(long n) {
            // back pressure path
            // this algorithm copied roughly from
            // rxjava-core/OnSubscribeFromIterable.java
            long previousCount = REQUESTED_UPDATER.getAndAdd(this, n);
            if (previousCount == 0) {
                while (true) {
                    long r = requested;
                    long numToEmit = r;

                    stack = Backpressure.search(condition, subscriber, stack, numToEmit);
                    if (stack.isEmpty()) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onCompleted();
                        else
                            return;
                    } else if (REQUESTED_UPDATER.addAndGet(this, -r) == 0)
                        return;
                }
            }
        }
    }

}
