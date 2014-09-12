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
import com.google.common.base.Preconditions;

final class NonLeaf<T> implements Node<T> {

    private final List<? extends Node<T>> children;
    private final Rectangle mbr;
    private final Context context;

    NonLeaf(List<? extends Node<T>> children, Context context) {
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
            Subscriber<? super Entry<T>> subscriber) {

        for (final Node<T> child : children) {
            if (subscriber.isUnsubscribed())
                return;
            else {
                if (criterion.call(child.geometry().mbr()))
                    child.search(criterion, subscriber);
            }
        }
    }

    @Override
    public String toString() {
        return "NonLeaf [mbr=" + mbr + "]";
    }

    @Override
    public int count() {
        return children.size();
    }

    public List<? extends Node<T>> children() {
        return children;
    }

    @Override
    public List<Node<T>> add(Entry<T> entry) {
        final Node<T> child = context.selector().select(entry.geometry().mbr(), children);
        List<Node<T>> list = child.add(entry);
        List<? extends Node<T>> children2 = Util.replace(children, child, list);
        if (children2.size() <= context.maxChildren())
            return Collections.singletonList((Node<T>) new NonLeaf<T>(children2, context));
        else {
            ListPair<? extends Node<T>> pair = context.splitter().split(children2,
                    context.minChildren());
            List<Node<T>> list2 = new ArrayList<Node<T>>();
            list2.add(new NonLeaf<T>(pair.group1().list(), context));
            list2.add(new NonLeaf<T>(pair.group2().list(), context));
            return list2;
        }
    }

    @Override
    public NodeAndEntries<T> delete(Entry<T> entry) {
        List<Entry<T>> addTheseEntries = new ArrayList<Entry<T>>();
        List<Node<T>> removeTheseNodes = new ArrayList<Node<T>>();
        List<Node<T>> addTheseNodes = new ArrayList<Node<T>>();
        for (final Node<T> child : children) {
            if (entry.geometry().intersects(child.geometry().mbr())) {
                final NodeAndEntries<T> result = child.delete(entry);
                if (result.node().isPresent())
                    addTheseNodes.add(result.node().get());
                removeTheseNodes.add(child);
                addTheseEntries.addAll(result.entries());
            }
        }
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<T>(of(this), Collections.<Entry<T>> emptyList());
        else {
            List<Node<T>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0)
                return new NodeAndEntries<T>(Optional.<Node<T>> absent(), addTheseEntries);
            else {
                NonLeaf<T> node = new NonLeaf<T>(nodes, context);
                return new NodeAndEntries<T>(of(node), addTheseEntries);
            }
        }
    }
}
