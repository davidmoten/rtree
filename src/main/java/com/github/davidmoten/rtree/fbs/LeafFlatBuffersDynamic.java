package com.github.davidmoten.rtree.fbs;


import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.LeafHelper;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeAndEntries;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.Subscriber;
import rx.functions.Func1;

public final class LeafFlatBuffersDynamic<T, S extends Geometry> implements Leaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], T> deserializer;

    public LeafFlatBuffersDynamic(List<Entry<T, S>> entries, Context<T, S> context,
            Func1<T, byte[]> serializer, Func1<byte[], T> deserializer) {
        this.context = context;
        this.deserializer = deserializer;
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        builder.finish(FlatBuffersHelper.addEntries(entries, builder, serializer));
        node = Node_.getRootAsNode_(builder.dataBuffer());
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
    public void search(Func1<? super Geometry, Boolean> condition,
            Subscriber<? super Entry<T, S>> subscriber) {
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
        Box_ b = node.mbb();
        // create on demand to reduce memory use (though not gc pressure)
        return Rectangle.create(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    @Override
    public List<Entry<T, S>> entries() {
        return FlatBuffersHelper.createEntries(node, deserializer);
    }

}
