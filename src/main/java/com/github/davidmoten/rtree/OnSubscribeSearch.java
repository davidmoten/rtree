package com.github.davidmoten.rtree;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;

final class OnSubscribeSearch<T> implements OnSubscribe<Entry<T>> {

    private final Node<T> node;
    private final Func1<? super Geometry, Boolean> criterion;

    OnSubscribeSearch(Node<T> node, Func1<? super Geometry, Boolean> criterion) {
        this.node = node;
        this.criterion = criterion;
    }

    @Override
    public void call(Subscriber<? super Entry<T>> subscriber) {
        node.search(criterion, subscriber);
        if (!subscriber.isUnsubscribed())
            subscriber.onCompleted();
    }

}
