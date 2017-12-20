package com.github.davidmoten.rtree.fbs;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.fbs.generated.BoundsType_;
import com.github.davidmoten.rtree.fbs.generated.Bounds_;
import com.github.davidmoten.rtree.fbs.generated.BoxDouble_;
import com.github.davidmoten.rtree.fbs.generated.BoxFloat_;
import com.github.davidmoten.rtree.fbs.generated.CircleDouble_;
import com.github.davidmoten.rtree.fbs.generated.CircleFloat_;
import com.github.davidmoten.rtree.fbs.generated.Entry_;
import com.github.davidmoten.rtree.fbs.generated.GeometryType_;
import com.github.davidmoten.rtree.fbs.generated.Geometry_;
import com.github.davidmoten.rtree.fbs.generated.LineDouble_;
import com.github.davidmoten.rtree.fbs.generated.LineFloat_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.fbs.generated.PointDouble_;
import com.github.davidmoten.rtree.fbs.generated.PointFloat_;
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
                if (p.isDoublePrecision()) {
                    geom = PointDouble_.createPointDouble_(builder, p.x(), p.y());
                    geomType = GeometryType_.PointDouble;
                } else {
                    geom = PointFloat_.createPointFloat_(builder, (float) p.x(), (float) p.y());
                    geomType = GeometryType_.PointFloat;
                }
            } else if (g instanceof Rectangle) {
                Rectangle b = (Rectangle) g;
                if (b.isDoublePrecision()) {
                    geom = BoxDouble_.createBoxDouble_(builder, b.x1(), b.y1(), b.x2(), b.y2());
                    geomType = GeometryType_.BoxDouble;
                } else {
                    geom = BoxFloat_.createBoxFloat_(builder, (float) b.x1(), (float) b.y1(),
                            (float) b.x2(), (float) b.y2());
                    geomType = GeometryType_.BoxFloat;
                }
            } else if (g instanceof Circle) {
                Circle c = (Circle) g;
                if (c.isDoublePrecision()) {
                    geom = CircleDouble_.createCircleDouble_(builder, c.x(), c.y(), c.radius());
                    geomType = GeometryType_.CircleDouble;
                } else {
                    geom = CircleFloat_.createCircleFloat_(builder, (float) c.x(), (float) c.y(),
                            (float) c.radius());
                    geomType = GeometryType_.CircleFloat;
                }
            } else if (g instanceof Line) {
                Line c = (Line) g;
                if (c.isDoublePrecision()) {
                    geom = LineDouble_.createLineDouble_(builder, c.x1(), c.y1(), c.x2(), c.y2());
                    geomType = GeometryType_.LineDouble;
                } else {
                    geom = LineFloat_.createLineFloat_(builder, (float) c.x1(), (float) c.y1(),
                            (float) c.x2(), (float) c.y2());
                    geomType = GeometryType_.LineFloat;
                }
            } else
                throw new RuntimeException("unexpected");

            Geometry_.startGeometry_(builder);
            if (geomType == GeometryType_.BoxFloat) {
                Geometry_.addBoxFloat(builder, geom);
            } else if (geomType == GeometryType_.BoxDouble) {
                Geometry_.addBoxDouble(builder, geom);
            } else if (geomType == GeometryType_.PointFloat) {
                Geometry_.addPointFloat(builder, geom);
            } else if (geomType == GeometryType_.PointDouble) {
                Geometry_.addPointDouble(builder, geom);
            } else if (geomType == GeometryType_.CircleFloat) {
                Geometry_.addCircleFloat(builder, geom);
            } else if (geomType == GeometryType_.CircleDouble) {
                Geometry_.addCircleDouble(builder, geom);
            } else if (geomType == GeometryType_.LineFloat) {
                Geometry_.addLineFloat(builder, geom);
            } else if (geomType == GeometryType_.LineDouble) {
                Geometry_.addLineDouble(builder, geom);
            } else
                throw new RuntimeException("unexpected");

            Geometry_.addType(builder, geomType);
            int geo = Geometry_.endGeometry_(builder);
            int obj = Entry_.createObjectVector(builder, serializer.call(entries.get(i).value()));
            entries2[i] = Entry_.createEntry_(builder, geo, obj);
        }

        int ents = Node_.createEntriesVector(builder, entries2);

        Rectangle mbb = Util.mbr(entries);
        Bounds_.startBounds_(builder);
        if (mbb.isDoublePrecision()) {
            int b = BoxDouble_.createBoxDouble_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
            Bounds_.addBoxDouble(builder, b);
            Bounds_.addType(builder, BoundsType_.BoundsDouble);
        } else {
            int b = BoxFloat_.createBoxFloat_(builder, (float) mbb.x1(), (float) mbb.y1(),
                    (float) mbb.x2(), (float) mbb.y2());
            Bounds_.addBoxFloat(builder, b);
            Bounds_.addType(builder, BoundsType_.BoundsFloat);
        }
        int bounds = Bounds_.endBounds_(builder);

        Node_.startNode_(builder);
        Node_.addMbb(builder, bounds);
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
        if (type == GeometryType_.BoxFloat) {
            result = createBox(g.boxFloat());
        } else if (type == GeometryType_.BoxDouble) {
            result = createBox(g.boxDouble());
        } else if (type == GeometryType_.PointFloat) {
            PointFloat_ p = g.pointFloat();
            result = Geometries.point(p.x(), p.y());
        } else if (type == GeometryType_.PointDouble) {
            PointDouble_ p = g.pointDouble();
            result = Geometries.point(p.x(), p.y());
        } else if (type == GeometryType_.CircleFloat) {
            CircleFloat_ c = g.circleFloat();
            result = Geometries.circle(c.x(), c.y(), c.radius());
        } else if (type == GeometryType_.CircleDouble) {
            CircleDouble_ c = g.circleDouble();
            result = Geometries.circle(c.x(), c.y(), c.radius());
        } else if (type == GeometryType_.LineFloat) {
            result = createLine(g.lineFloat());
        } else if (type == GeometryType_.LineDouble) {
            result = createLine(g.lineDouble());
        } else
            throw new RuntimeException("unexpected");
        return (S) result;
    }

    private static Geometry createBox(BoxDouble_ b) {
        return Geometries.rectangle(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    private static Geometry createBox(BoxFloat_ b) {
        return Geometries.rectangle(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    static Rectangle createBox(Bounds_ b) {
        if (b.type() == BoundsType_.BoundsDouble) {
            BoxDouble_ r = b.boxDouble();
            return Geometries.rectangle(r.minX(), r.minY(), r.maxX(), r.maxY());
        } else {
            BoxFloat_ r = b.boxFloat();
            return Geometries.rectangle(r.minX(), r.minY(), r.maxX(), r.maxY());
        }
    }

    static Line createLine(BoxFloat_ b) {
        return Geometries.line(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

    static Line createLine(BoxDouble_ b) {
        return Geometries.line(b.minX(), b.minY(), b.maxX(), b.maxY());
    }

}