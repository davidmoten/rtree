package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    T value();

    @Override
    S geometry();

}