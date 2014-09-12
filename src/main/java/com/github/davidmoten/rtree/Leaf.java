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
    public String toString() {
        return "Leaf [entries=" + entries + ", mbr=" + mbr + "]";
    }

    @Override
    public int count() {
        return entries.size();
    }

    @Override
    public List<Node<T>> add(Entry<T> entry) {
        final List<Entry<T>> children2 = Util.add(entries, entry);
        if (children2.size() <= context.maxChildren())
            return Collections.singletonList((Node<T>) new Leaf<T>(children2, context));
        else {
            ListPair<Entry<T>> pair = context.splitter().split(children2, context.minChildren());
            List<Node<T>> list2 = new ArrayList<Node<T>>();
            list2.add(new Leaf<T>(pair.group1().list(), context));
            list2.add(new Leaf<T>(pair.group2().list(), context));
            return list2;
        }
    }

    @Override
    public NodeAndEntries<T> delete(Entry<T> entry) {
        if (!entries.contains(entry)) {
            return new NodeAndEntries<T>(Optional.of(this), Collections.<Entry<T>> emptyList());
        } else {
            final List<Entry<T>> entries2 = Util.remove(entries, entry);
            if (entries2.size() >= context.minChildren()) {
                Leaf<T> node = new Leaf<T>(entries2, context);
                return new NodeAndEntries<T>(of(node), Collections.<Entry<T>> emptyList());
            } else {
                return new NodeAndEntries<T>(Optional.<Node<T>> absent(), entries2);
            }
        }
    }
}
