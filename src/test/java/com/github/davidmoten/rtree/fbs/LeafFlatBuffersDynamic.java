package com.github.davidmoten.rtree.fbs;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.EntryDefault;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.LeafHelper;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeAndEntries;
import com.github.davidmoten.rtree.flatbuffers.Box_;
import com.github.davidmoten.rtree.flatbuffers.GeometryType_;
import com.github.davidmoten.rtree.flatbuffers.Geometry_;
import com.github.davidmoten.rtree.flatbuffers.Node_;
import com.github.davidmoten.rtree.flatbuffers.Point_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.Subscriber;
import rx.functions.Func1;

public final class LeafFlatBuffersDynamic<T, S extends Geometry> implements Leaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;

    public LeafFlatBuffersDynamic(List<Entry<T, S>> entries, Context<T, S> context,
            Func1<T, byte[]> serializer) {
        this.context = context;
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Entry<T, S>> entries() {
        List<Entry<T, S>> list = new ArrayList<Entry<T, S>>(node.entriesLength());
        for (int i = 0; i < node.entriesLength(); i++) {
            Geometry_ g = node.entries(i).geometry();
            final Geometry geometry;
            if (g.type() == GeometryType_.Box) {
                Box_ b = g.box();
                geometry = Rectangle.create(b.minX(), b.minY(), b.maxX(), b.maxY());
            } else if (g.type() == GeometryType_.Point) {
                Point_ p = g.point();
                geometry = Point.create(p.x(), p.y());
            } else
                throw new RuntimeException("unexpected");
            node.entries(i).object(i);
            list.add(EntryDefault.<T, S> entry((T) new Object(), (S) geometry));
        }
        return list;
    }

}
