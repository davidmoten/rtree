package com.github.davidmoten.rtree;

import rx.Subscriber;

import com.github.davidmoten.util.ImmutableStack;

public interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);

	void search(Rectangle r, Subscriber<? super Entry> subscriber);

	void children(Subscriber<? super Node> subscriber);
}
