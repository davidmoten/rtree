package com.github.davidmoten.rtree;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

public class OnSubscribeSearch implements OnSubscribe<Entry> {

	private final Node node;
	private final Rectangle rectangle;

	public OnSubscribeSearch(Node node, Rectangle rectangle) {
		this.node = node;
		this.rectangle = rectangle;
	}

	@Override
	public void call(Subscriber<? super Entry> subscriber) {
		node.search(rectangle, subscriber);
		if (!subscriber.isUnsubscribed())
			subscriber.onCompleted();
	}

}
