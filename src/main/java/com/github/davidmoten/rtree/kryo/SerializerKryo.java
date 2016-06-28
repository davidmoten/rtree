package com.github.davidmoten.rtree.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import rx.functions.Func0;
import rx.functions.Func1;

public class SerializerKryo<T, S extends Geometry> extends Serializer<RTree<T,S>> {

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
    public RTree<T, S> read(Kryo arg0, Input arg1, Class<RTree<T, S>> arg2) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public void write(Kryo arg0, Output arg1, RTree<T, S> arg2) {
        // TODO Auto-generated method stub
        
    }

    public static <T, S extends Geometry> Serializer<T, S> create(
            Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer,
            Func0<Kryo> kryoFactory) {
        return new SerializerKryo<T, S>(serializer, deserializer, kryoFactory);
    }

    private static class RTreeSerializer<T, S extends Geometry>
            extends com.esotericsoftware.kryo.Serializer<RTree<T, S>> {

        @Override
        public void write(Kryo kryo, Output output, RTree<T, S> tree) {
            writeContext(tree.context(), output);
            output.writeBoolean(tree.root().isPresent());
            if (tree.root().isPresent()) {
                writeNode(tree.root().get(), output);
            }
        }

        private void writeNode(Node<T, S> node, Output output) {
            boolean isLeaf = node instanceof Leaf;
            output.writeBoolean(isLeaf);
            if (isLeaf) {
                Leaf<T, S> leaf = (Leaf<T, S>) node;
                output.writeInt(leaf.count());
                writeBounds(leaf.geometry().mbr(), output);
            }
        }

        private void writeBounds(Rectangle mbr, Output output) {
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
        public RTree<T, S> read(Kryo kryo, Input input, Class<RTree<T, S>> type) {
            // TODO Auto-generated method stub
            return null;
        }

    }
    
}
