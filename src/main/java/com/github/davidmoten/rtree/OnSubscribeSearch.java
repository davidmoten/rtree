package com.github.davidmoten.rtree;

import java.util.concurrent.atomic.AtomicLong;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.util.ImmutableStack;
import com.github.davidmoten.rx.util.BackpressureUtils;

import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

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

    @VisibleForTesting
    static class SearchProducer<T, S extends Geometry> implements Producer {

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
            // TODO remove this patch that exists because of RxJava PR 3727
            if (n == Long.MAX_VALUE - 1)
                n = Long.MAX_VALUE;
            try {
                if (n <= 0 || requested.get() == Long.MAX_VALUE)
                    // none requested or already started with fast path
                    return;
                else if (n == Long.MAX_VALUE && requested.compareAndSet(0, Long.MAX_VALUE)) {
                    // fast path
                    requestAll();
                } else
                    requestSome(n);
            } catch (RuntimeException e) {
                subscriber.onError(e);
            }
        }

        private void requestAll() {
            node.searchWithoutBackpressure(condition, subscriber);
            if (!subscriber.isUnsubscribed())
                subscriber.onCompleted();
        }

        private void requestSome(long n) {
            // back pressure path
            // this algorithm copied roughly from
            // rxjava-core/OnSubscribeFromIterable.java

            // rxjava used AtomicLongFieldUpdater instead of AtomicLong
            // but benchmarks showed no benefit here so reverted to AtomicLong
            long previousCount = BackpressureUtils.getAndAddRequest(requested, n);
            if (previousCount == 0) {
                // don't touch stack every time during the loop because
                // is a volatile and every write forces a thread memory
                // cache flush
                ImmutableStack<NodePosition<T, S>> st = stack;
                while (true) {
                    // minimize atomic reads by assigning to a variable here
                    long r = requested.get();
                    st = Backpressure.search(condition, subscriber, st, r);
                    if (st.isEmpty()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onCompleted();
                            break;
                        } else
                            break;
                    } else if (requested.addAndGet(-r) == 0)
                        break;
                }
                stack = st;
            }
        }
    }

}
