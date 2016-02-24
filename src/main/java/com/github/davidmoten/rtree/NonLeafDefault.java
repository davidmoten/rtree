package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Subscriber;
import rx.functions.Func1;

final class NonLeafDefault<T, S extends Geometry> implements NonLeaf<T, S> {

    private final List<? extends Node<T, S>> children;
    private final Rectangle mbr;
    private final Context<T, S> context;

    NonLeafDefault(List<? extends Node<T, S>> children, Context<T, S> context) {
        Preconditions.checkArgument(!children.isEmpty());
        this.context = context;
        this.children = children;
        this.mbr = Util.mbr(children);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        NonLeafMethods.search(criterion, subscriber, this);
    }

    @Override
    public int count() {
        return NonLeafMethods.count(this);
    }

    @Override
    public List<? extends Node<T, S>> children() {
        return children;
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return NonLeafMethods.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return NonLeafMethods.delete(entry, all, this);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }
}