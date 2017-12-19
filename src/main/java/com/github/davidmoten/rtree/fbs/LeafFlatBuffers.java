package com.github.davidmoten.rtree.fbs;

import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.fbs.generated.BoundsType_;
import com.github.davidmoten.rtree.fbs.generated.Bounds_;
import com.github.davidmoten.rtree.fbs.generated.BoxDouble_;
import com.github.davidmoten.rtree.fbs.generated.BoxFloat_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.LeafHelper;
import com.github.davidmoten.rtree.internal.NodeAndEntries;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.Subscriber;
import rx.functions.Func1;

final class LeafFlatBuffers<T, S extends Geometry> implements Leaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], ? extends T> deserializer;

    LeafFlatBuffers(List<Entry<T, S>> entries, Context<T, S> context,
            Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        this(createNode(entries, serializer), context, deserializer);
    }

    LeafFlatBuffers(Node_ node, Context<T, S> context, Func1<byte[], ? extends T> deserializer) {
        this.context = context;
        this.deserializer = deserializer;
        this.node = node;
    }

    private static <T, S extends Geometry> Node_ createNode(List<Entry<T, S>> entries,
            Func1<? super T, byte[]> serializer) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        builder.finish(FlatBuffersHelper.addEntries(entries, builder, serializer));
        return Node_.getRootAsNode_(builder.dataBuffer());
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
        // only called when the root of the tree is a Leaf
        // normally the searchWithoutBackpressure is executed completely within the
        // NonLeafFlatBuffers class to reduce object creation
        LeafHelper.search(condition, subscriber, this);
    }

    @Override
    public int count() {
        return node.entriesLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        Bounds_ b = node.mbb();
        // create on demand to reduce memory use (though not gc pressure)
        if (b.type() == BoundsType_.BoundsDouble) {
            BoxDouble_ r = b.boxDouble();
            return Geometries.rectangle(r.minX(), r.minY(), r.maxX(), r.maxY());
        } else {
            BoxFloat_ r = b.boxFloat();
            return Geometries.rectangle(r.minX(), r.minY(), r.maxX(), r.maxY());
        }
    }

    @Override
    public List<Entry<T, S>> entries() {
        return FlatBuffersHelper.createEntries(node, deserializer);
    }

    @Override
    public Entry<T, S> entry(int i) {
        return FlatBuffersHelper.createEntry(node, deserializer, i);
    }

}
