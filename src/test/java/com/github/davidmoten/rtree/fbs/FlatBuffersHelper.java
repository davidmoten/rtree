package com.github.davidmoten.rtree.fbs;

import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Util;
import com.github.davidmoten.rtree.flatbuffers.Box_;
import com.github.davidmoten.rtree.flatbuffers.Entry_;
import com.github.davidmoten.rtree.flatbuffers.GeometryType_;
import com.github.davidmoten.rtree.flatbuffers.Geometry_;
import com.github.davidmoten.rtree.flatbuffers.Node_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

class FlatBuffersHelper {

    static <T, S extends Geometry> int addEntries(List<Entry<T, S>> entries, FlatBufferBuilder builder) {
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
        return Node_.endNode_(builder);
    }
}
