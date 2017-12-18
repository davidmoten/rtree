package com.github.davidmoten.rtree.fbs;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Circle_;
import com.github.davidmoten.rtree.fbs.generated.Entry_;
import com.github.davidmoten.rtree.fbs.generated.GeometryType_;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.fbs.generated.Line_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.fbs.generated.Point_;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.Util;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.functions.Func1;

final class FlatBuffersHelper {

    private FlatBuffersHelper() {
        // prevent instantiation
    }

    static <T, S extends Geometry> int addEntries(List<Entry<T, S>> entries,
            FlatBufferBuilder builder, Func1<? super T, byte[]> serializer) {
        int[] entries2 = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Geometry g = entries.get(i).geometry();
            final int geom;
            final byte geomType;
            // Must check Point before Rectangle because Point is instance of
            // Rectangle
            if (g instanceof Point) {
                Point p = (Point) g;
                geom = Point_.createPoint_(builder, p.x(), p.y());
                geomType = GeometryType_.Point;
            } else if (g instanceof Rectangle) {
                Rectangle b = (Rectangle) g;
                geom = Box_.createBox_(builder, (float) b.x1(), (float) b.y1(), (float) b.x2(),
                        (float) b.y2());
                geomType = GeometryType_.Box;
            } else if (g instanceof Circle) {
                Circle c = (Circle) g;
                geom = Circle_.createCircle_(builder, c.x(), c.y(), c.radius());
                geomType = GeometryType_.Circle;
            } else if (g instanceof Line) {
                Line c = (Line) g;
                geom = Line_.createLine_(builder, c.x1(), c.y1(), c.x2(), c.y2());
                geomType = GeometryType_.Line;
            } else
                throw new RuntimeException("unexpected");

            Geometry_.startGeometry_(builder);
            if (geomType == GeometryType_.Box) {
                Geometry_.addBox(builder, geom);
            } else if (geomType == GeometryType_.Point) {
                Geometry_.addPoint(builder, geom);
            } else if (geomType == GeometryType_.Circle) {
                Geometry_.addCircle(builder, geom);
            } else if (geomType == GeometryType_.Line) {
                Geometry_.addLine(builder, geom);
            } else
                throw new RuntimeException("unexpected");

            Geometry_.addType(builder, geomType);
            int geo = Geometry_.endGeometry_(builder);
            int obj = Entry_.createObjectVector(builder, serializer.call(entries.get(i).value()));
            entries2[i] = Entry_.createEntry_(builder, geo, obj);
        }

        int ents = Node_.createEntriesVector(builder, entries2);
        Rectangle mbb = Util.mbr(entries);
        int b = Box_.createBox_(builder, (float) mbb.x1(), (float) mbb.y1(),(float)  mbb.x2(),(float)  mbb.y2());
        Node_.startNode_(builder);
        Node_.addMbb(builder, b);
        Node_.addEntries(builder, ents);
        return Node_.endNode_(builder);

    }

    static <T, S extends Geometry> List<Entry<T, S>> createEntries(Node_ node,
            Func1<byte[], ? extends T> deserializer) {
        int numEntries = node.entriesLength();
        List<Entry<T, S>> entries = new ArrayList<Entry<T, S>>(numEntries);
        Preconditions.checkArgument(numEntries > 0);
        Entry_ entry = new Entry_();
        Geometry_ geom = new Geometry_();
        for (int i = 0; i < numEntries; i++) {
            Entry<T, S> ent = createEntry(node, deserializer, entry, geom, i);
            entries.add(ent);
        }
        return entries;
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends Geometry> Entry<T, S> createEntry(Node_ node,
            Func1<byte[], ? extends T> deserializer, Entry_ entry, Geometry_ geom, int i) {
        node.entries(entry, i);
        entry.geometry(geom);
        final Geometry g = toGeometry(geom);
        return Entries.entry(parseObject(deserializer, entry), (S) g);
    }

    static <T, S extends Geometry> Entry<T, S> createEntry(Node_ node,
            Func1<byte[], ? extends T> deserializer, int i) {
        return createEntry(node, deserializer, new Entry_(), new Geometry_(), i);
    }

    static <T> T parseObject(Func1<byte[], ? extends T> deserializer, Entry_ entry) {
        ByteBuffer bb = entry.objectAsByteBuffer();
        if (bb == null) {
            return null;
        } else {
            byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
            T t = deserializer.call(bytes);
            return t;
        }
    }

    @SuppressWarnings("unchecked")
    static <S extends Geometry> S toGeometry(Geometry_ g) {
        final Geometry result;
        byte type = g.type();
        if (type == GeometryType_.Box) {
            result = createBox(g.box());
        } else if (type == GeometryType_.Point) {
            Point_ p = g.point();
            result = Geometries.point(p.x(), p.y());
        } else if (type == GeometryType_.Circle) {
            Circle_ c = g.circle();
            result = Geometries.circle(c.x(), c.y(), c.radius());
        } else if (type == GeometryType_.Line) {
            result = createLine(g.line());
        } else
            throw new RuntimeException("unexpected");
        return (S) result;
    }

    static Rectangle createBox(Box_ b) {
        return Geometries.rectangle(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    static Line createLine(Box_ b) {
        return Geometries.line(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

}