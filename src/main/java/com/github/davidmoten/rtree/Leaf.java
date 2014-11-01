package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.of;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.ListPair;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.base.Optional;

final class Leaf<T, S extends Geometry> implements Node<T, S> {

    private final List<Entry<T, S>> entries;
    private final Rectangle mbr;
    private final Context context;

    Leaf(List<Entry<T, S>> entries, Context context) {
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    List<Entry<T, S>> entries() {
        return entries;
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {

        if (!condition.call(this.geometry().mbr()))
            return;

        for (final Entry<T, S> entry : entries) {
            if (subscriber.isUnsubscribed())
                return;
            else {
                if (condition.call(entry.geometry()))
                    subscriber.onNext(entry);
            }
        }
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        @SuppressWarnings("unchecked")
        final List<Entry<T, S>> entries2 = Util.add(entries, (Entry<T, S>) entry);
        if (entries2.size() <= context.maxChildren())
            return Collections.singletonList((Node<T, S>) new Leaf<T, S>(entries2, context));
        else {
            ListPair<Entry<T, S>> pair = context.splitter().split(entries2, context.minChildren());
            return makeLeaves(pair);
        }
    }

    private List<Node<T, S>> makeLeaves(ListPair<Entry<T, S>> pair) {
        List<Node<T, S>> list = new ArrayList<Node<T, S>>();
        list.add(new Leaf<T, S>(pair.group1().list(), context));
        list.add(new Leaf<T, S>(pair.group2().list(), context));
        return list;
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<T, S>(of(this), Collections.<Entry<T, S>> emptyList(), 0);
        } else {
            final List<Entry<T, S>> entries2 = new ArrayList<Entry<T, S>>(entries);
            entries2.remove(entry);
            int numDeleted = 1;
            // keep deleting if all specified
            while (all && entries2.remove(entry))
                numDeleted += 1;

            if (entries2.size() >= context.minChildren()) {
                Leaf<T, S> node = new Leaf<T, S>(entries2, context);
                return new NodeAndEntries<T, S>(of(node), Collections.<Entry<T, S>> emptyList(),
                        numDeleted);
            } else {
                return new NodeAndEntries<T, S>(Optional.<Node<T, S>> absent(), entries2,
                        numDeleted);
            }
        }
    }

}
