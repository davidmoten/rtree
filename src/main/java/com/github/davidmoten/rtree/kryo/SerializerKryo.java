package com.github.davidmoten.rtree.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.functions.Func0;
import rx.functions.Func1;

public class SerializerKryo<T, S extends Geometry> implements Serializer<T, S> {

    private final Func1<? super T, byte[]> serializer;
    private final Func1<byte[], ? extends T> deserializer;
    private final Func0<Kryo> kryoFactory;

    public SerializerKryo(Func1<? super T, byte[]> serializer,
            Func1<byte[], ? extends T> deserializer, Func0<Kryo> kryoFactory) {
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.kryoFactory = kryoFactory;
    }

    @Override
    public void write(RTree<T, S> tree, OutputStream os) throws IOException {
        Output output = new Output(os);
        Kryo kryo = kryoFactory.call();
        write(kryo, output, tree);
    }

    private void write(Kryo kryo, Output output, RTree<T, S> tree) {
        writeContext(tree.context(), output);
        output.writeBoolean(tree.root().isPresent());
        if (tree.root().isPresent()) {
            writeNode(tree.root().get(), output, false);
        }
    }

    private void writeNode(Node<T, S> node, Output output, boolean hasParent) {
        boolean isLeaf = node instanceof Leaf;
        output.writeBoolean(isLeaf);
        if (isLeaf) {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            writeBounds(output, leaf.geometry().mbr());
            output.writeInt(leaf.count());
            for (Entry<T, S> entry : leaf.entries()) {
                S g = entry.geometry();
                writeValue(output, entry.value());
                writeGeometry(output, g);
            }
        } else {

            NonLeaf<T, S> nonLeaf = (NonLeaf<T, S>) node;
        }
        output.writeBoolean(hasParent);
    }

    private void writeValue(Output output, T t) {
        byte[] bytes = serializer.call(t);
        output.write(bytes.length);
        output.write(bytes);
    }

    private void writeRectangle(Output output, S g) {
        Rectangle r = (Rectangle) g;
        output.write(0);
        writeBounds(output, r);
    }

    private void writeGeometry(Output output, S g) {
        if (g instanceof Rectangle) {
            writeRectangle(output, g);
        } else {
            throw new RuntimeException("unexpected");
        }
    }

    private void writeBounds(Output output, Rectangle mbr) {
        output.writeFloat(mbr.x1());
        output.writeFloat(mbr.y1());
        output.writeFloat(mbr.y1());
        output.writeFloat(mbr.y2());
    }

    private void writeContext(Context<T, S> context, Output output) {
        output.writeInt(context.minChildren());
        output.writeInt(context.maxChildren());
    }

    @Override
    public RTree<T, S> read(InputStream is, long sizeBytes, InternalStructure structure)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    public static <T, S extends Geometry> Serializer<T, S> create(
            Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer,
            Func0<Kryo> kryoFactory) {
        return new SerializerKryo<T, S>(serializer, deserializer, kryoFactory);
    }

}
