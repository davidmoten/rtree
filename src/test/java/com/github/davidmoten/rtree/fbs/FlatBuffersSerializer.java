package com.github.davidmoten.rtree.fbs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.google.flatbuffers.FlatBufferBuilder;

public class FlatBuffersSerializer {

    public <T, S extends Geometry> void serialize(RTree<T, S> tree, OutputStream os) {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int n = addNode(tree.root().get(), builder);
    }

    private static <T, S extends Geometry> int addNode(Node<T, S> node, FlatBufferBuilder builder) {
        if (node instanceof Leaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return FlatBuffersHelper.addEntries(leaf.entries(), builder);
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            List<Integer> list = new ArrayList<Integer>(nonLeaf.count());
            for (Node<T, S> child : nonLeaf.children()) {
                list.add(addNode(child, builder));
            }
            // TODO
            return -1;
        }
    }

    public <T, S extends Geometry> RTree<T, S> deserialize(InputStream is) {
        return null;
    }

}
