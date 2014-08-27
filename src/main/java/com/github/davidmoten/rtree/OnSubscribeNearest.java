package com.github.davidmoten.rtree;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

final class OnSubscribeNearest implements OnSubscribe<Entry> {

	private final Node node;
	private final int k;
	private final Rectangle r;

	public OnSubscribeNearest(Node node, Rectangle r, int k) {
		this.node = node;
		this.r = r;
		this.k = k;
	}

	@Override
	public void call(Subscriber<? super Entry> subscriber) {
		node.nearest(r, k, subscriber);
		if (!subscriber.isUnsubscribed())
			subscriber.onCompleted();
	}

}
