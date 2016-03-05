package com.github.davidmoten.rtree.fbs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;

public interface Serializer<T, S extends Geometry> {

    void serialize(RTree<T, S> tree, OutputStream os) throws IOException;

    RTree<T, S> deserialize(long sizeBytes, InputStream is, InternalStructure structure)
            throws IOException;

}