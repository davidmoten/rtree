package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasMbr;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Optional;

interface Node<T> extends HasMbr {

	@Override
	Rectangle mbr();

	Node<T> add(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack);

	void search(Func1<? super Geometry, Boolean> criterion,
			Subscriber<? super Entry<T>> subscriber);

	Optional<Node<T>> delete(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack);

}
