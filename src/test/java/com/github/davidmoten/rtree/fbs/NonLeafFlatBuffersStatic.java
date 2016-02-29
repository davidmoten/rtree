package com.github.davidmoten.rtree.fbs;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.LeafDefault;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeAndEntries;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.NonLeafDefault;
import com.github.davidmoten.rtree.flatbuffers.Box_;
import com.github.davidmoten.rtree.flatbuffers.GeometryType_;
import com.github.davidmoten.rtree.flatbuffers.Geometry_;
import com.github.davidmoten.rtree.flatbuffers.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.Subscriber;
import rx.functions.Func1;

public class NonLeafFlatBuffersStatic<T, S extends Geometry> implements NonLeaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], T> deserializer;

    NonLeafFlatBuffersStatic(Node_ node, Context<T, S> context, Func1<byte[], T> deserializer) {
        Preconditions.checkArgument(node.childrenLength() > 0 || node.entriesLength() > 0);
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
            List<Node<T, S>> children = createChildren();
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

    private List<Node<T, S>> createChildren() {
        List<Node<T, S>> children = new ArrayList<Node<T, S>>(node.childrenLength());
        for (int i = 0; i < node.childrenLength(); i++) {
            children.add(
                    new NonLeafFlatBuffersStatic<T, S>(node.children(i), context, deserializer));
        }
        return children;
    }

    private Entry<T, S> createEntry(final int index) {
        return new Entry<T, S>() {

            @Override
            public T value() {
                ByteBuffer bb = node.entries(index).objectAsByteBuffer();
                byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.remaining());
                return deserializer.call(bytes);
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
        return createBox(node.mbb());
    }

    private static Geometry createBox(Box_ b) {
        return Rectangle.create(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    @Override
    public List<? extends Node<T, S>> children() {
        return createChildren();
    }

    @SuppressWarnings("unchecked")
    private static <S extends Geometry> S toGeometry(Geometry_ g) {
        final Geometry result;
        if (g.type() == GeometryType_.Box) {
            result = createBox(g.box());
        } else if (g.type() == GeometryType_.Point) {
            result = Point.create(g.point().x(), g.point().y());
        } else
            throw new UnsupportedOperationException();
        return (S) result;
    }

}
