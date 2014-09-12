package com.github.davidmoten.rtree;

import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;

interface Node<T> extends HasGeometry {

    List<Node<T>> add(Entry<T> entry);

    NodeAndEntries<T> delete(Entry<T> entry);

    void search(Func1<? super Geometry, Boolean> condition, Subscriber<? super Entry<T>> subscriber);

    int count();

}
