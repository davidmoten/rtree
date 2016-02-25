package com.github.davidmoten.rtree.flatbuffers;

import java.util.List;

import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NodeFactory;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.geometry.Geometry;

public class NodeFactoryFlatBuffers<T, S extends Geometry> implements NodeFactory<T, S> {

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        // TODO
        return null;
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        // TODO Auto-generated method stub
        return null;
    }

}
