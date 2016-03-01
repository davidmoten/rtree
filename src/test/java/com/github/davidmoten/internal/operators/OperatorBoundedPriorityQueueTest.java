package com.github.davidmoten.internal.operators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.internal.operators.OperatorBoundedPriorityQueue;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class OperatorBoundedPriorityQueueTest {

    private static Comparator<Integer> integerComparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer i1, Integer i2) {
            return i1.compareTo(i2);
        }
    };

    @Test
    public void testPriority() {
        List<Integer> list = Observable.range(1, 5)
                .lift(new OperatorBoundedPriorityQueue<Integer>(2, integerComparator)).toSortedList()
                .toBlocking().single();
        assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void testUnsubscribeAfterFirst() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        Observable.range(1, 5)
                // go through priority queue
                .lift(new OperatorBoundedPriorityQueue<Integer>(2, integerComparator))
                // subscribe
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onCompleted() {
                        completed.set(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer t) {
                        unsubscribe();
                    }
                });
        assertFalse(completed.get());
    }

    @Test
    public void testUnsubscribeAfterLastButBeforeCompletedCalled() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        Observable.range(1, 5)
                // go through priority queue
                .lift(new OperatorBoundedPriorityQueue<Integer>(2, integerComparator))
                // subscribe
                .subscribe(new Subscriber<Integer>() {

                    int i = 0;

                    @Override
                    public void onCompleted() {
                        completed.set(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer t) {
                        i++;
                        if (i == 2)
                            unsubscribe();
                    }
                });
        assertFalse(completed.get());
    }

    @Test
    public void testError() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final AtomicBoolean error = new AtomicBoolean(false);
        Observable.<Integer> error(new RuntimeException())
                // go through priority queue
                .lift(new OperatorBoundedPriorityQueue<Integer>(2, integerComparator))
                // subscribe
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onCompleted() {
                        completed.set(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        error.set(true);
                    }

                    @Override
                    public void onNext(Integer t) {

                    }
                });
        assertFalse(completed.get());
        assertTrue(error.get());
    }

    @Test
    public void testErrorCalledJustAfterUnsubscribe() {
        final AtomicBoolean error = new AtomicBoolean(false);
        final Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                error.set(true);
            }

            @Override
            public void onNext(Integer t) {
            }
        };
        Observable.create(new OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> sub) {
                sub.onNext(1);
                subscriber.unsubscribe();
                sub.onError(new RuntimeException());
            }
        })
                // go through priority queue
                .lift(new OperatorBoundedPriorityQueue<Integer>(1, integerComparator))
                // subscribe
                .subscribe(subscriber);
        assertFalse(error.get());
    }

    @Test
    public void testUnsubscribeCalledAfterFirst() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        final AtomicBoolean next = new AtomicBoolean(false);
        final Subscriber<Integer> subscriber = new Subscriber<Integer>() {

            @Override
            public void onCompleted() {
                completed.set(true);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                next.set(true);
            }
        };
        Observable.create(new OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> sub) {
                sub.onNext(1);
                subscriber.unsubscribe();
                sub.onNext(2);
                sub.onCompleted();
            }
        })
                // go through priority queue
                .lift(new OperatorBoundedPriorityQueue<Integer>(1, integerComparator))
                // subscribe
                .subscribe(subscriber);
        assertFalse(completed.get());
        assertFalse(next.get());
    }

    @Test(timeout = 3000)
    public void testOperatorShouldRequestMaxFromUpstream() {
        RTree<String, Line> tree = RTree.star().create();
        for (int i = 0; i < 5; ++i) {
            tree = tree.add(String.format("Hello %d", i), Geometries.line(-i, -i, 5 + i, i));
        }
        tree.nearest(Geometries.point(2, 0.4), Double.MAX_VALUE, 1).toBlocking().single();
    }

}
