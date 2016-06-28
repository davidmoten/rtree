package com.github.davidmoten.rtree.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Serializer;
import com.github.davidmoten.rtree.geometry.Geometry;

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
        Kryo k = kryoFactory.call();
    }

    @Override
    public RTree<T, S> read(InputStream is, long sizeBytes, InternalStructure structure)
            throws IOException {
        Kryo k = kryoFactory.call();
        return null;
    }

    public static <T, S extends Geometry> Serializer<T, S> create(
            Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer,
            Func0<Kryo> kryoFactory) {
        return new SerializerKryo<T, S>(serializer, deserializer, kryoFactory);
    }

}
