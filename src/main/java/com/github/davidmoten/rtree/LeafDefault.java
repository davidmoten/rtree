package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Subscriber;
import rx.functions.Func1;

final class LeafDefault<T, S extends Geometry> implements Leaf<T, S> {

    private final List<Entry<T, S>> entries;
    private final Rectangle mbr;
    private final Context<T, S> context;

    LeafDefault(List<Entry<T, S>> entries, Context<T, S> context) {
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public List<Entry<T, S>> entries() {
        return entries;
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        LeafMethods.search(condition, subscriber, this);
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafMethods.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafMethods.delete(entry, all, this);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

}
