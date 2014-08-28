package com.github.davidmoten.rtree;

import java.util.Comparator;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

import com.google.common.base.Optional;

final class OnSubscribeSearch implements OnSubscribe<Entry> {

	private final Node node;
	private final Func1<? super Rectangle, Boolean> criterion;
	private final Optional<Comparator<? super Rectangle>> comparator;

	OnSubscribeSearch(Node node, Func1<? super Rectangle, Boolean> criterion,
			Optional<Comparator<? super Rectangle>> comparator) {
		this.node = node;
		this.criterion = criterion;
		this.comparator = comparator;
	}

	@Override
	public void call(Subscriber<? super Entry> subscriber) {
		node.search(criterion, subscriber, comparator);
		if (!subscriber.isUnsubscribed())
			subscriber.onCompleted();
	}

}
