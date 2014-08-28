package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

final class OnSubscribeSearch implements OnSubscribe<Entry> {

	private final Node node;
	private final Func1<? super Geometry, Boolean> criterion;

	OnSubscribeSearch(Node node, Func1<? super Geometry, Boolean> criterion) {
		this.node = node;
		this.criterion = criterion;
	}

	@Override
	public void call(Subscriber<? super Entry> subscriber) {
		node.search(criterion, subscriber);
		if (!subscriber.isUnsubscribed())
			subscriber.onCompleted();
	}

}
