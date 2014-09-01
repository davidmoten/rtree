package com.github.davidmoten.rtree;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Optional;

interface Node<T> extends HasGeometry {

    Node<T> add(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack);

    void search(Func1<? super Geometry, Boolean> condition, Subscriber<? super Entry<T>> subscriber);

    Optional<Node<T>> delete(Entry<T> entry, ImmutableStack<NonLeaf<T>> stack);

    int count();

}
