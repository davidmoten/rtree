package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.internal.NodeAndEntries;

import rx.Subscriber;
import rx.functions.Func1;

public interface Node<T, S extends Geometry> extends HasGeometry {

    List<Node<T, S>> add(Entry<? extends T, ? extends S> entry);

    NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all);

    /**
     * Run when a search requests Long.MAX_VALUE results. This is the
     * no-backpressure fast path.
     * 
     * @param criterion
     *            function that returns true if the geometry is a search match
     * @param subscriber
     *            the subscriber to report search findings to
     */
    void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber);

    int count();

    Context<T, S> context();

}
