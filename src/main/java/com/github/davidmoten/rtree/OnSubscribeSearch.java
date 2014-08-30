package com.github.davidmoten.rtree;

import rx.Observable.OnSubscribe;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.util.ImmutableStack;

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
        subscriber.setProducer(new SearchProducer<T>(node, subscriber));
    }

    private static class SearchProducer<T> implements Producer {

        private final Subscriber<? super Entry<T>> subscriber;
        private final Node<T> node;
        private volatile ImmutableStack<NodePosition<T>> stack;

        public SearchProducer(Node<T> node, Subscriber<? super Entry<T>> subscriber) {
            this.node = node;
            this.subscriber = subscriber;
            stack = ImmutableStack.create(new NodePosition<T>(node, 0));
        }

        @Override
        public void request(long n) {

        }

    }

}
