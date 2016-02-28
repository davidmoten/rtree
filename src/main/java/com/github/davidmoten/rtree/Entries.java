package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;

public final class Entries {

    public static <T, S extends Geometry> Entry<T,S> entry(T object, S geometry) {
        return EntryDefault.entry(object, geometry);
    }
    
}
