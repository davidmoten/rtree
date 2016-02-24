package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class LeafFactoryImpl<T, S extends Geometry> implements LeafFactory<T, S> {

    @Override
    public Leaf<T, S> create(List<Entry<T, S>> entries, Context context) {
        return new LeafImpl<T, S>(entries, context);
    }

}
