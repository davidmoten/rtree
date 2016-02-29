package com.github.davidmoten.rtree.fbs;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.LeafDefault;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeAndEntries;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.NonLeafDefault;
import com.github.davidmoten.rtree.flatbuffers.Geometry_;
import com.github.davidmoten.rtree.flatbuffers.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;

import rx.Subscriber;
import rx.functions.Func1;

public class NonLeafFlatBuffersStatic<T, S extends Geometry> implements NonLeaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], T> deserializer;

    NonLeafFlatBuffersStatic(Node_ node, Context<T, S> context, Func1<byte[], T> deserializer) {
        this.node = node;
        this.context = context;
        this.deserializer = deserializer;
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        final Node<T, S> nd;
        if (node.childrenLength() > 0) {
            List<Node<T, S>> children = new ArrayList<Node<T, S>>(node.childrenLength());
            for (int i = 0; i < node.childrenLength(); i++) {
                children.add(new NonLeafFlatBuffersStatic<T, S>(node.children(i), context,
                        deserializer));
            }
            nd = new NonLeafDefault<T, S>(children, context);
        } else {
            List<Entry<T, S>> entries = new ArrayList<Entry<T, S>>(node.entriesLength());
            for (int i = 0; i < node.entriesLength(); i++) {
                final int index = i;
                entries.add(createEntry(index));
            }
            nd = new LeafDefault<T, S>(entries, context);
        }
        nd.search(condition, subscriber);
    }

    private Entry<T, S> createEntry(final int index) {
        return new Entry<T, S>() {

            @Override
            public T value() {
                return deserializer.call(node.entries(index).objectAsByteBuffer().array());
            }

            @Override
            public S geometry() {
                Geometry_ g = node.entries(index).geometry();
                return toGeometry(g);
            }
        };
    }

    @Override
    public int count() {
        return node.childrenLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends Node<T, S>> children() {
        // TODO Auto-generated method stub
        return null;
    }

    private static <S extends Geometry> S toGeometry(Geometry_ g) {
        // TODO
        return null;
    }

}
