package com.github.davidmoten.rtree;

import java.util.concurrent.atomic.AtomicLong;

import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

final class OnSubscribeSearch<T, S extends Geometry> implements OnSubscribe<Entry<T, S>> {

    private final Node<T, S> node;
    private final Func1<? super Geometry, Boolean> condition;

    OnSubscribeSearch(Node<T, S> node, Func1<? super Geometry, Boolean> condition) {
        this.node = node;
        this.condition = condition;
    }

    @Override
    public void call(Subscriber<? super Entry<T, S>> subscriber) {
        subscriber.setProducer(new SearchProducer<T, S>(node, condition, subscriber));
    }

    private static class SearchProducer<T, S extends Geometry> implements Producer {

        private final Subscriber<? super Entry<T, S>> subscriber;
        private final Node<T, S> node;
        private final Func1<? super Geometry, Boolean> condition;
        private volatile ImmutableStack<NodePosition<T, S>> stack;
        private final AtomicLong requested = new AtomicLong(0);

        SearchProducer(Node<T, S> node, Func1<? super Geometry, Boolean> condition,
                Subscriber<? super Entry<T, S>> subscriber) {
            this.node = node;
            this.condition = condition;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<T, S>(node, 0));
        }

        @Override
        public void request(long n) {
            try {
                if (requested.get() == Long.MAX_VALUE)
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
            requested.set(Long.MAX_VALUE);
            node.search(condition, subscriber);
            if (!subscriber.isUnsubscribed())
                subscriber.onCompleted();
        }

        private void requestSome(long n) {
            // back pressure path
            // this algorithm copied roughly from
            // rxjava-core/OnSubscribeFromIterable.java

            // rxjava used AtomicLongFieldUpdater instead of AtomicLong
            // but benchmarks showed no benefit here so reverted to AtomicLong
            long previousCount = requested.getAndAdd(n);
            if (previousCount == 0) {
                while (true) {
                    long r = requested.get();
                    long numToEmit = r;

                    stack = Backpressure.search(condition, subscriber, stack, numToEmit);
                    if (stack.isEmpty()) {
                        if (!subscriber.isUnsubscribed())
                            subscriber.onCompleted();
                        else
                            return;
                    } else if (requested.addAndGet(-r) == 0)
                        return;
                }
            }
        }
    }

}
