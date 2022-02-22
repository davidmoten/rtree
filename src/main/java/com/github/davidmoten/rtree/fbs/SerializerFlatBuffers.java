package com.github.davidmoten.rtree.fbs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.SelectorRStar;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.SerializerHelper;
import com.github.davidmoten.rtree.SplitterRStar;
import com.github.davidmoten.rtree.fbs.generated.BoundsType_;
import com.github.davidmoten.rtree.fbs.generated.Bounds_;
import com.github.davidmoten.rtree.fbs.generated.BoxDouble_;
import com.github.davidmoten.rtree.fbs.generated.BoxFloat_;
import com.github.davidmoten.rtree.fbs.generated.Context_;
import com.github.davidmoten.rtree.fbs.generated.Node_;
import com.github.davidmoten.rtree.fbs.generated.Tree_;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.LeafDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;
import com.google.flatbuffers.FlatBufferBuilder;

import rx.functions.Func1;

public final class SerializerFlatBuffers<T, S extends Geometry> implements Serializer<T, S> {

    private final FactoryFlatBuffers<T, S> factory;

    private SerializerFlatBuffers(Func1<? super T, byte[]> serializer,
            Func1<byte[], ? extends T> deserializer) {
        this.factory = new FactoryFlatBuffers<T, S>(serializer, deserializer);
    }

    public static <T, S extends Geometry> Serializer<T, S> create(
            Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        return new SerializerFlatBuffers<T, S>(serializer, deserializer);
    }

    @Override
    public void write(RTree<T, S> tree, OutputStream os) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder();
        final Rectangle mbb;
        if (tree.root().isPresent()) {
            mbb = tree.root().get().geometry().mbr();
        } else {
            mbb = Geometries.rectangle(0, 0, 0, 0);
        }
        int b = toBounds(builder, mbb);
        Context_.startContext_(builder);
        Context_.addBounds(builder, b);
        Context_.addMinChildren(builder, tree.context().minChildren());
        Context_.addMaxChildren(builder, tree.context().maxChildren());
        int c = Context_.endContext_(builder);
        final int n;
        if (tree.root().isPresent()) {
            n = addNode(tree.root().get(), builder, factory.serializer());
        } else {
            // won't be used
            n = 0;
        }
        // int t = Tree_.createTree_(builder, c, n, tree.size());
        Tree_.startTree_(builder);
        Tree_.addContext(builder, c);
        Tree_.addSize(builder, tree.size());
        if (tree.size() > 0) {
            Tree_.addRoot(builder, n);
        }
        int t = Tree_.endTree_(builder);
        Tree_.finishTree_Buffer(builder, t);

        ByteBuffer bb = builder.dataBuffer();
        os.write(bb.array(), bb.position(), bb.remaining());
    }

    private static int toBounds(FlatBufferBuilder builder, final Rectangle r) {
        Bounds_.startBounds_(builder);
        if (r.isDoublePrecision()) {
            Bounds_.addType(builder, BoundsType_.BoundsDouble);
            int box = BoxDouble_.createBoxDouble_(builder, r.x1(), r.y1(), r.x2(), r.y2());
            Bounds_.addBoxDouble(builder, box);
        } else {
            Bounds_.addType(builder, BoundsType_.BoundsFloat);
            int box = BoxFloat_.createBoxFloat_(builder, (float) r.x1(), (float) r.y1(),
                    (float) r.x2(), (float) r.y2());
            Bounds_.addBoxFloat(builder, box);
        }
        return Bounds_.endBounds_(builder);
    }

    private static <T, S extends Geometry> int addNode(Node<T, S> node, FlatBufferBuilder builder,
            Func1<? super T, byte[]> serializer) {
        if (node instanceof Leaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            return FlatBuffersHelper.addEntries(leaf.entries(), builder, serializer);
        } else {
            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
            int[] nodes = new int[nonLeaf.count()];
            for (int i = 0; i < nonLeaf.count(); i++) {
                Node<T, S> child = nonLeaf.child(i);
                nodes[i] = addNode(child, builder, serializer);
            }
            int ch = Node_.createChildrenVector(builder, nodes);
            Rectangle mbb = nonLeaf.geometry().mbr();
            int b = toBounds(builder, mbb);
            Node_.startNode_(builder);
            Node_.addChildren(builder, ch);
            Node_.addMbb(builder, b);
            return Node_.endNode_(builder);
        }
    }

    @Override
    public RTree<T, S> read(InputStream is, long sizeBytes, InternalStructure structure)
            throws IOException {
        byte[] bytes = readFully(is, (int) sizeBytes);
        Tree_ t = Tree_.getRootAsTree_(ByteBuffer.wrap(bytes));
        Context<T, S> context = new Context<T, S>(t.context().minChildren(),
                t.context().maxChildren(), new SelectorRStar(), new SplitterRStar(), factory);
        Node_ node = t.root();
        if (node == null) {
            return SerializerHelper.create(Optional.empty(), 0, context);
        } else {
            final Node<T, S> root;
            if (structure == InternalStructure.SINGLE_ARRAY) {
                if (node.childrenLength() > 0) {
                    root = new NonLeafFlatBuffers<T, S>(node, context, factory.deserializer());
                } else {
                    root = new LeafFlatBuffers<T, S>(node, context, factory.deserializer());
                }
            } else {
                root = toNodeDefault(node, context, factory.deserializer());
            }
            return SerializerHelper.create(Optional.of(root), (int) t.size(), context);
        }
    }

    private static <T, S extends Geometry> Node<T, S> toNodeDefault(Node_ node,
            Context<T, S> context, Func1<byte[], ? extends T> deserializer) {
        int numChildren = node.childrenLength();
        if (numChildren > 0) {
            List<Node<T, S>> children = new ArrayList<Node<T, S>>(numChildren);
            for (int i = 0; i < numChildren; i++) {
                children.add(toNodeDefault(node.children(i), context, deserializer));
            }
            return new NonLeafDefault<T, S>(children, context);
        } else {
            List<Entry<T, S>> entries = FlatBuffersHelper.createEntries(node, deserializer);
            return new LeafDefault<T, S>(entries, context);
        }
    }

    @VisibleForTesting
    static byte[] readFully(InputStream is, int numBytes) throws IOException {
        byte[] b = new byte[numBytes];
        int count = 0;
        do {
            int n = is.read(b, count, numBytes - count);
            if (n > 0) {
                count += n;
            } else {
                throw new RuntimeException("unexpected");
            }
        } while (count < numBytes);
        return b;
    }

}
