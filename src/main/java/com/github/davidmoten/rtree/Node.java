package com.github.davidmoten.rtree;

import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;

interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    void search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber);

    int count();

}
