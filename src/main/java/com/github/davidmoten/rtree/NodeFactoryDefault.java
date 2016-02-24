package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometry;

public class NodeFactoryDefault<T, S extends Geometry> implements NodeFactory<T, S> {

    private static class Holder {
        private static final NodeFactory<Object, Geometry> INSTANCE = new NodeFactoryDefault<Object, Geometry>();
    }

    @SuppressWarnings("unchecked")
    public static <T, S extends Geometry> NodeFactory<T, S> instance() {
        return (NodeFactory<T, S>) Holder.INSTANCE;
    }

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        return new LeafDefault<T, S>(entries, context);
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        return new NonLeafDefault<T, S>(children, context);
    }

}
