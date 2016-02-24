package com.github.davidmoten.rtree;

import static com.github.davidmoten.guavamini.Optional.of;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.ListPair;

import rx.Subscriber;
import rx.functions.Func1;

public class NonLeafMethods {

    public static <T, S extends Geometry> void search(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber, NonLeaf<T, S> node) {
        if (!criterion.call(node.geometry().mbr()))
            return;

        for (final Node<T, S> child : node.children()) {
            if (subscriber.isUnsubscribed())
                return;
            else
                child.search(criterion, subscriber);
        }
    }

    public static <T, S extends Geometry> List<Node<T, S>> add(
            Entry<? extends T, ? extends S> entry, NonLeaf<T, S> node) {
        Context context = node.context();
        final Node<T, S> child = context.selector().select(entry.geometry().mbr(), node.children());
        List<Node<T, S>> list = child.add(entry);
        List<? extends Node<T, S>> children2 = Util.replace(node.children(), child, list);
        if (children2.size() <= context.maxChildren())
            return Collections
                    .singletonList((Node<T, S>) new NonLeafImpl<T, S>(children2, context));
        else {
            ListPair<? extends Node<T, S>> pair = context.splitter().split(children2,
                    context.minChildren());
            return makeNonLeaves(pair, context);
        }
    }

    private static <T, S extends Geometry> List<Node<T, S>> makeNonLeaves(
            ListPair<? extends Node<T, S>> pair, Context context) {
        List<Node<T, S>> list = new ArrayList<Node<T, S>>();
        list.add(new NonLeafImpl<T, S>(pair.group1().list(), context));
        list.add(new NonLeafImpl<T, S>(pair.group2().list(), context));
        return list;
    }

    public static <T, S extends Geometry> int count(NonLeaf<T, S> nonLeaf) {
        return nonLeaf.children().size();
    }

    public static <T, S extends Geometry> NodeAndEntries<T, S> delete(
            Entry<? extends T, ? extends S> entry, boolean all, NonLeaf<T, S> node) {
        // the result of performing a delete of the given entry from this node
        // will be that zero or more entries will be needed to be added back to
        // the root of the tree (because num entries of their node fell below
        // minChildren),
        // zero or more children will need to be removed from this node,
        // zero or more nodes to be added as children to this node(because
        // entries have been deleted from them and they still have enough
        // members to be active)
        List<Entry<T, S>> addTheseEntries = new ArrayList<Entry<T, S>>();
        List<Node<T, S>> removeTheseNodes = new ArrayList<Node<T, S>>();
        List<Node<T, S>> addTheseNodes = new ArrayList<Node<T, S>>();
        int countDeleted = 0;
        List<? extends Node<T, S>> children = node.children();
        for (final Node<T, S> child : children) {
            if (entry.geometry().intersects(child.geometry().mbr())) {
                final NodeAndEntries<T, S> result = child.delete(entry, all);
                if (result.node().isPresent()) {
                    if (result.node().get() != child) {
                        // deletion occurred and child is above minChildren so
                        // we update it
                        addTheseNodes.add(result.node().get());
                        removeTheseNodes.add(child);
                        addTheseEntries.addAll(result.entriesToAdd());
                        countDeleted += result.countDeleted();
                        if (!all)
                            break;
                    }
                    // else nothing was deleted from that child
                } else {
                    // deletion occurred and brought child below minChildren
                    // so we redistribute its entries
                    removeTheseNodes.add(child);
                    addTheseEntries.addAll(result.entriesToAdd());
                    countDeleted += result.countDeleted();
                    if (!all)
                        break;
                }
            }
        }
        if (removeTheseNodes.isEmpty())
            return new NodeAndEntries<T, S>(of(node), Collections.<Entry<T, S>> emptyList(), 0);
        else {
            List<Node<T, S>> nodes = Util.remove(children, removeTheseNodes);
            nodes.addAll(addTheseNodes);
            if (nodes.size() == 0)
                return new NodeAndEntries<T, S>(Optional.<Node<T, S>> absent(), addTheseEntries,
                        countDeleted);
            else {
                NonLeafImpl<T, S> nd = new NonLeafImpl<T, S>(nodes, node.context());
                return new NodeAndEntries<T, S>(of(nd), addTheseEntries, countDeleted);
            }
        }
    }

}
