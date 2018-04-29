package com.github.davidmoten.rtree.fbs;

import java.util.List;

import org.reactivestreams.Subscriber;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.FlowableSearch.SearchSubscription;
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

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

final class LeafFlatBuffers<T, S extends Geometry> implements Leaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Function<byte[], ? extends T> deserializer;

    LeafFlatBuffers(List<Entry<T, S>> entries, Context<T, S> context,
            Function<? super T, byte[]> serializer, Function<byte[], ? extends T> deserializer) throws Exception {
        this(createNode(entries, serializer), context, deserializer);
    }

    LeafFlatBuffers(Node_ node, Context<T, S> context, Function<byte[], ? extends T> deserializer) {
        this.context = context;
        this.deserializer = deserializer;
        this.node = node;
    }

    private static <T, S extends Geometry> Node_ createNode(List<Entry<T, S>> entries,
            Function<? super T, byte[]> serializer) throws Exception {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        builder.finish(FlatBuffersHelper.addEntries(entries, builder, serializer));
        return Node_.getRootAsNode_(builder.dataBuffer());
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry) throws Exception {
        return LeafHelper.add(entry, this);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) throws Exception {
        return LeafHelper.delete(entry, all, this);
    }

    @Override
    public void searchWithoutBackpressure(Predicate<? super Geometry> condition,
            Subscriber<? super Entry<T, S>> subscriber, SearchSubscription<T, S> searchSubscription) throws Exception {
        // only called when the root of the tree is a Leaf
        // normally the searchWithoutBackpressure is executed completely within the
        // NonLeafFlatBuffers class to reduce object creation
        LeafHelper.search(condition, subscriber, this, searchSubscription);
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
    public List<Entry<T, S>> entries() throws Exception {
        return FlatBuffersHelper.createEntries(node, deserializer);
    }

    @Override
    public Entry<T, S> entry(int i) throws Exception {
        return FlatBuffersHelper.createEntry(node, deserializer, i);
    }

}
