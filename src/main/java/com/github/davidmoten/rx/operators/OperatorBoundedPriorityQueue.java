package com.github.davidmoten.rx.operators;

import java.util.Comparator;

import com.google.common.collect.MinMaxPriorityQueue;

import rx.Observable.Operator;
import rx.Subscriber;

public final class OperatorBoundedPriorityQueue<T> implements Operator<T, T> {

    private final int maximumSize;
    private final Comparator<? super T> comparator;

    public OperatorBoundedPriorityQueue(int maximumSize, Comparator<? super T> comparator) {
        this.maximumSize = maximumSize;
        this.comparator = comparator;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        final MinMaxPriorityQueue<T> q = MinMaxPriorityQueue.orderedBy(comparator)
                .maximumSize(maximumSize).create();
        return new Subscriber<T>(child) {

            @Override
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override
            public void onCompleted() {
                while (true) {
                    T t = q.poll();
                    if (t == null)
                        break;
                    else if (!isUnsubscribed())
                        child.onNext(t);
                    else
                        return;
                }
                if (isUnsubscribed())
                    return;
                child.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                if (!isUnsubscribed())
                    child.onError(t);
            }

            @Override
            public void onNext(T t) {
                if (!isUnsubscribed())
                    q.add(t);
            }
        };
    }

}
