package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func2;

import com.github.davidmoten.util.ImmutableStack;

interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);

	void search(Rectangle r, Subscriber<? super Entry> subscriber);

	void entries(Subscriber<? super Entry> subscriber);

	void nearest(Rectangle r, int k, Subscriber<? super Entry> subscriber);

	void nearest(Rectangle r, int k,
			Func2<Rectangle, Rectangle, Double> distanceFunction,
			Subscriber<? super Entry> subscriber);
}
