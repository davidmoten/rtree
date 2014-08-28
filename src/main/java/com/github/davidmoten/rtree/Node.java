package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasMbr;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.util.ImmutableStack;

interface Node extends HasMbr {

	@Override
	Rectangle mbr();

	Node add(Entry entry, ImmutableStack<NonLeaf> stack);

	void search(Func1<? super Geometry, Boolean> criterion,
			Subscriber<? super Entry> subscriber);

}
