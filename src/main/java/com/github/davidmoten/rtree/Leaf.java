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

final class Leaf<T> implements Node<T> {

    private final List<Entry<T>> entries;
    private final Rectangle mbr;
    private final Context context;

    Leaf(List<Entry<T>> entries, Context context) {
        this.entries = entries;
        this.context = context;
        this.mbr = Util.mbr(entries);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    List<Entry<T>> entries() {
        return entries;
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T>> subscriber) {

        for (final Entry<T> entry : entries) {
            if (subscriber.isUnsubscribed())
                return;
            else {
                if (criterion.call(entry.geometry()))
                    subscriber.onNext(entry);
            }
        }
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T>> add(Entry<T> entry) {
        final List<Entry<T>> entries2 = Util.add(entries, entry);
        if (entries2.size() <= context.maxChildren())
            return Collections.singletonList((Node<T>) new Leaf<T>(entries2, context));
        else {
            ListPair<Entry<T>> pair = context.splitter().split(entries2, context.minChildren());
            return makeLeaves(pair);
        }
    }

    private List<Node<T>> makeLeaves(ListPair<Entry<T>> pair) {
        List<Node<T>> list = new ArrayList<Node<T>>();
        list.add(new Leaf<T>(pair.group1().list(), context));
        list.add(new Leaf<T>(pair.group2().list(), context));
        return list;
    }

    @Override
    public NodeAndEntries<T> delete(Entry<T> entry, boolean all) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<T>(Optional.of(this), Collections.<Entry<T>> emptyList(), 0);
        } else {
            final List<Entry<T>> entries2 = new ArrayList<Entry<T>>(entries);
            entries2.remove(entry);
            int numDeleted = 1;
            // keep deleting if all specified
            while (all && entries2.remove(entry))
                numDeleted += 1;

            if (entries2.size() >= context.minChildren()) {
                Leaf<T> node = new Leaf<T>(entries2, context);
                return new NodeAndEntries<T>(of(node), Collections.<Entry<T>> emptyList(),
                        numDeleted);
            } else {
                return new NodeAndEntries<T>(Optional.<Node<T>> absent(), entries2, numDeleted);
            }
        }
    }
}
