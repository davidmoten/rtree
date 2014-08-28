package com.github.davidmoten.rtree;

import java.util.Comparator;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Optional;

interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);

	void search(Func1<? super Rectangle, Boolean> criterion,
			Subscriber<? super Entry> subscriber,
			Optional<Comparator<? super Rectangle>> comparator);

}
