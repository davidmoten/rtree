package com.github.davidmoten.rtree.flatbuffers;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.LeafHelper;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeAndEntries;
import com.github.davidmoten.rtree.Util;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.Subscriber;
import rx.functions.Func1;

public class LeafFlatBuffers<T, S extends Geometry> implements Leaf<T, S> {

    private final Node_ node;
    private final Context<T, S> context;

    public LeafFlatBuffers(List<Entry<T, S>> entries, Context<T, S> context) {
        this.context = context;
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int[] entries2 = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Rectangle b = entries.get(i).geometry().mbr();
            int box = Box_.createBox_(builder, b.x1(), b.y1(), b.x2(), b.y2());
            Geometry_.startGeometry_(builder);
            Geometry_.addBox(builder, box);
            Geometry_.addType(builder, GeometryType_.Box);
            int g = Geometry_.endGeometry_(builder);
            int obj = Entry_.createObjectVector(builder, new byte[] { 'b', 'o', 'o' });
            entries2[i] = Entry_.createEntry_(builder, g, obj);
        }
        int ents = Node_.createEntriesVector(builder, entries2);
        Rectangle mbb = Util.mbr(entries);
        int b = Box_.createBox_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
        Node_.startNode_(builder);
        Node_.addMbb(builder, b);
        Node_.addEntries(builder, ents);
        Node_.endNode_(builder);
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
            list.add(Entry.<T, S> entry((T) new Object(), (S) geometry));
        }
        return list;
    }

}
