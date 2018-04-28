package com.github.davidmoten.rtree;

import java.util.concurrent.atomic.AtomicLong;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.util.ImmutableStack;

import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;

public final class FlowableSearch<T, S extends Geometry>
        extends Flowable<com.github.davidmoten.rtree.Entry<T, S>> {

    private final Node<T, S> node;
    private final Predicate<? super Geometry> condition;

    public FlowableSearch(Node<T, S> node, Predicate<? super Geometry> condition) {
        this.node = node;
        this.condition = condition;
    }

    @Override
    protected void subscribeActual(Subscriber<? super Entry<T, S>> s) {
        SearchSubscription<T, S> subscription = new SearchSubscription<T, S>(node, condition, s);
        s.onSubscribe(subscription);
    }

    public static final class SearchSubscription<T, S extends Geometry> implements Subscription {

        private final Subscriber<? super Entry<T, S>> subscriber;
        private final Node<T, S> node;
        private final Predicate<? super Geometry> condition;
        private volatile ImmutableStack<NodePosition<T, S>> stack;
        private final AtomicLong requested = new AtomicLong(0);
        private volatile boolean cancelled;

        public SearchSubscription(Node<T, S> node, Predicate<? super Geometry> condition,
                Subscriber<? super Entry<T, S>> subscriber) {
            this.node = node;
            this.condition = condition;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<T, S>(node, 0));
        }

        @Override
        public void request(long n) {
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
            if (!cancelled) {
                subscriber.onComplete();
            }
        }

        private void requestSome(long n) {
            // back pressure path
            // this algorithm copied roughly from
            // rxjava-core/OnSubscribeFromIterable.java

            // rxjava used AtomicLongFieldUpdater instead of AtomicLong
            // but benchmarks showed no benefit here so reverted to AtomicLong
            long previousCount = getAndAddRequest(requested, n);
            if (previousCount == 0) {
                // don't touch stack every time during the loop because
                // is a volatile and every write forces a thread memory
                // cache flush
                ImmutableStack<NodePosition<T, S>> st = stack;
                while (true) {
                    // minimize atomic reads by assigning to a variable here
                    long r = requested.get();
                    try {
                        st = Backpressure.search(condition, subscriber, st, r, this);
                    } catch (Exception e) {
                        if (!cancelled) {
                            subscriber.onError(e);
                            return;
                        }
                    }
                    if (st.isEmpty()) {
                        // release some state for gc (although empty stack so not very significant)
                        stack = null;
                        if (!cancelled) {
                            subscriber.onComplete();
                        }
                        return;
                    } else {
                        stack = st;
                        if (requested.addAndGet(-r) == 0)
                            return;
                    }
                }

            }
        }

        /**
         * Adds {@code n} to {@code requested} and returns the value prior to addition
         * once the addition is successful (uses CAS semantics). If overflows then sets
         * {@code requested} field to {@code Long.MAX_VALUE}.
         * 
         * @param requested
         *            atomic field updater for a request count
         * @param n
         *            the number of requests to add to the requested count
         * @return requested value just prior to successful addition
         */
        private static long getAndAddRequest(AtomicLong requested, long n) {
            // add n to field but check for overflow
            while (true) {
                long current = requested.get();
                long next = current + n;
                // check for overflow
                if (next < 0) {
                    next = Long.MAX_VALUE;
                }
                if (requested.compareAndSet(current, next)) {
                    return current;
                }
            }
        }

        @Override
        public void cancel() {
             cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

    }

}
