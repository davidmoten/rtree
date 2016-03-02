package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;

public interface Entry<T, S extends Geometry> extends HasGeometry {

    /**
     * Returns the value wrapped by this {@link EntryDefault}.
     * 
     * @return the entry value
     */
    T value();

    S geometry();

}