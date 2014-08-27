package com.github.davidmoten.rtree;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

final class OnSubscribeEntries implements OnSubscribe<Entry> {

	private final Node node;

	OnSubscribeEntries(Node node) {
		this.node = node;
	}

	@Override
	public void call(Subscriber<? super Entry> subscriber) {
		node.entries(subscriber);
		if (subscriber.isUnsubscribed())
			return;
		subscriber.onCompleted();
	}

}
