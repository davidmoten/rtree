package com.github.davidmoten.rtree.fbs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.github.davidmoten.guavamini.Optional;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.LeafDefault;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.SelectorRStar;
import com.github.davidmoten.rtree.SerializerHelper;
import com.github.davidmoten.rtree.SplitterRStar;
import com.github.davidmoten.rtree.fbs.generated.Box_;
import com.github.davidmoten.rtree.fbs.generated.Context_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.fbs.generated.Tree_;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.functions.Func1;

public class FlatBuffersSerializer {

    public <T, S extends Geometry> void serialize(RTree<T, S> tree, Func1<T, byte[]> serializer,
            OutputStream os) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        int n = addNode(tree.root().get(), builder, serializer);

        Rectangle mbb = tree.root().get().geometry().mbr();
        int b = Box_.createBox_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
        Context_.startContext_(builder);
        Context_.addBounds(builder, b);
        Context_.addMinChildren(builder, tree.context().minChildren());
        Context_.addMaxChildren(builder, tree.context().maxChildren());
        int c = Context_.endContext_(builder);

        int t = Tree_.createTree_(builder, c, n, tree.size());
        Tree_.finishTree_Buffer(builder, t);
        ByteBuffer bb = builder.dataBuffer();
        os.write(bb.array(), bb.position(), bb.remaining());
    }

    private static <T, S extends Geometry> int addNode(Node<T, S> node, FlatBufferBuilder builder,
            Func1<T, byte[]> serializer) {
        if (node instanceof Leaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return FlatBuffersHelper.addEntries(leaf.entries(), builder, serializer);
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            int[] nodes = new int[nonLeaf.count()];
            for (int i = 0; i < nonLeaf.count(); i++) {
                Node<T, S> child = nonLeaf.children().get(i);
                nodes[i] = addNode(child, builder, serializer);
            }
            int ch = Node_.createChildrenVector(builder, nodes);
            Node_.startNode_(builder);
            Node_.addChildren(builder, ch);
            Rectangle mbb = nonLeaf.geometry().mbr();
            int b = Box_.createBox_(builder, mbb.x1(), mbb.y1(), mbb.x2(), mbb.y2());
            Node_.addMbb(builder, b);
            return Node_.endNode_(builder);
        }
    }

    public <T, S extends Geometry> RTree<T, S> deserialize(long sizeBytes, InputStream is,
            Func1<byte[], T> deserializer) throws IOException {
        byte[] bytes = readFully(is, (int) sizeBytes);
        Tree_ t = Tree_.getRootAsTree_(ByteBuffer.wrap(bytes));
        Node_ node = t.root();
        Context<T, S> context = new Context<T, S>(t.context().minChildren(),
                t.context().maxChildren(), new SelectorRStar(), new SplitterRStar(),
                new FactoryImmutable<T, S>());
        final Node<T, S> root;
        if (node.childrenLength() > 0)
            root = new NonLeafFlatBuffersStatic<T, S>(node, context, deserializer);
        else {
            List<Entry<T, S>> entries = FlatBuffersHelper.createEntries(node, deserializer);
            root = new LeafDefault<T, S>(entries, context);
        }
        return SerializerHelper.create(Optional.of(root), (int) t.size(), context);
    }

    private static byte[] readFully(InputStream is, int numBytes) throws IOException {
        byte[] b = new byte[numBytes];
        int n = is.read(b);
        if (n != numBytes)
            throw new RuntimeException("unexpected");
        return b;
    }

}
