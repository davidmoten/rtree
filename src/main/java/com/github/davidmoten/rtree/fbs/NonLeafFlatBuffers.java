package com.github.davidmoten.rtree.fbs;

import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.createBox;
import static com.github.davidmoten.rtree.fbs.FlatBuffersHelper.toGeometry;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Entry_;
import com.github.davidmoten.rtree.fbs.generated.GeometryType_;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.LeafDefault;
import com.github.davidmoten.rtree.internal.NodeAndEntries;

import rx.Subscriber;
import rx.functions.Func1;

final class NonLeafFlatBuffers<T, S extends Geometry> implements NonLeaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;
    private final Func1<byte[], T> deserializer;

    NonLeafFlatBuffers(Node_ node, Context<T, S> context, Func1<byte[], T> deserializer) {
        Preconditions.checkNotNull(node);
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
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        // pass through entry and geometry and box instances to be reused for
        // flatbuffers extraction this reduces allocation/gc costs (but of
        // course introduces some mutable ugliness into the codebase)
        search(node, criterion, subscriber, deserializer, new Entry_(), new Geometry_(),
                new Box_());
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> void search(Node_ node,
            Func1<? super Geometry, Boolean> criterion, Subscriber<? super Entry<T, S>> subscriber,
            Func1<byte[], T> deserializer, Entry_ entry, Geometry_ geometry, Box_ box) {
        {
            node.mbb(box);
            if (!criterion
                    .call(Geometries.rectangle(box.minX(), box.minY(), box.maxX(), box.maxY())))
                return;
        }
        int numChildren = node.childrenLength();
        // reduce allocations by reusing objects
        Node_ child = new Node_();
        if (numChildren > 0) {
            for (int i = 0; i < numChildren; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                node.children(child, i);
                search(child, criterion, subscriber, deserializer, entry, geometry, box);
            }
        } else {
            int numEntries = node.entriesLength();
            // reduce allocations by reusing objects
            // check all entries
            for (int i = 0; i < numEntries; i++) {
                if (subscriber.isUnsubscribed())
                    return;
                // set entry
                node.entries(entry, i);
                // set geometry
                entry.geometry(geometry);
                final Geometry g = toGeometry(geometry);
                if (criterion.call(g)) {
                    T t = parseObject(deserializer, entry);
                    Entry<T, S> ent = Entries.entry(t, (S) g);
                    subscriber.onNext(ent);
                }
            }
        }

    }

    private static <T> T parseObject(Func1<byte[], T> deserializer, Entry_ entry) {
        ByteBuffer bb = entry.objectAsByteBuffer();
        byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
        T t = deserializer.call(bytes);
        return t;
    }

    private List<Node<T, S>> createChildren() {
        List<Node<T, S>> children = new ArrayList<Node<T, S>>(node.childrenLength());
        // reduce allocations by resusing objects
        int numChildren = node.childrenLength();
        for (int i = 0; i < numChildren; i++) {
            Node_ child = node.children(i);
            if (child.childrenLength()>0) {
                children.add(new NonLeafFlatBuffers<T, S>(child, context, deserializer));
            } else {
                children.add(new LeafFlatBuffers<T,S>(child, context, deserializer));
            }
        }
        return children;
    }

    @Override
    public int count() {
        int childrenCount = node.childrenLength();
        if (childrenCount > 0)
            return childrenCount;
        else
            return node.entriesLength();
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Geometry geometry() {
        return FlatBuffersHelper.createBox(node.mbb());
    }


    @Override
    public Node<T, S> child(int i) {
        Node_ child = node.children(i);
        if (child.childrenLength() > 0)
            return new NonLeafFlatBuffers<T, S>(child, context, deserializer);
        else
            return new LeafDefault<T, S>(
                    NonLeafFlatBuffers.<T, S> createEntries(child, deserializer), context);
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> List<Entry<T, S>> createEntries(Node_ node,
            Func1<byte[], T> deserializer) {
        List<Entry<T, S>> entries = new ArrayList<Entry<T, S>>();
        int numEntries = node.entriesLength();
        Preconditions.checkArgument(numEntries > 0);
        Entry_ entry = new Entry_();
        Geometry_ geom = new Geometry_();
        for (int i = 0; i < numEntries; i++) {
            node.entries(entry, i);
            entry.geometry(geom);
            final Geometry g = toGeometry(geom);
            entries.add(Entries.entry(parseObject(deserializer, entry), (S) g));
        }
        return entries;
    }

    @Override
    public List<Node<T, S>> children() {
        return createChildren();
    }

    @Override
    public String toString() {
        return "Node [" + (node.childrenLength() > 0 ? "NonLeaf" : "Leaf") + ","
                + createBox(node.mbb()).toString() + "]";
    }

}
