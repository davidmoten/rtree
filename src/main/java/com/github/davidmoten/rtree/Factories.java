package com.github.davidmoten.rtree;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.FactoryDefault;

public final class Factories {

    private Factories() {
        // prevent instantiation
    }

    public static <T, S extends Geometry> Factory<T, S> defaultFactory() {
        return FactoryDefault.instance();
    }
}
