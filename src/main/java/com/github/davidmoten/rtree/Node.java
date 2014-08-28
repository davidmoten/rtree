package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.util.ImmutableStack;

interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);

	void search(Func1<? super Rectangle, Boolean> criterion,
			Subscriber<? super Entry> subscriber);

}
