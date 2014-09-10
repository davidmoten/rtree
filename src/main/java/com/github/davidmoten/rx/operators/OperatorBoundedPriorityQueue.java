package com.github.davidmoten.rx.operators;

import java.util.Comparator;

import rx.Observable.Operator;
import rx.Subscriber;

import com.google.common.collect.MinMaxPriorityQueue;

public final class OperatorBoundedPriorityQueue<T> implements Operator<T, T> {

    private final int maximumSize;
    private final Comparator<T> comparator;

    public OperatorBoundedPriorityQueue(int maximumSize, Comparator<T> comparator) {
        this.maximumSize = maximumSize;
        this.comparator = comparator;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> child) {
        final MinMaxPriorityQueue<T> q = MinMaxPriorityQueue.orderedBy(comparator)
                .maximumSize(maximumSize).create();
        return new Subscriber<T>(child) {

            @Override
            public void onCompleted() {
                for (T t : q) {
                    if (isUnsubscribed())
                        return;
                    child.onNext(t);
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
