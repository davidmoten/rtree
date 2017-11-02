package com.github.davidmoten.rtree.fbs;


import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entries;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Factory;
import com.github.davidmoten.rtree.Leaf;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.internal.FactoryDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;

import rx.functions.Func1;

/**
 * Conserves memory in comparison to {@link FactoryDefault} especially for
 * larger {@code maxChildren} by saving Leaf objects to byte arrays and using
 * FlatBuffers to access the byte array.
 *
 * @param <T>
 *            the object type
 * @param <S>
 *            the geometry type
 */
public final class FactoryFlatBuffers<T, S extends Geometry> implements Factory<T, S> {
    private final Func1<? super T, byte[]> serializer;
    private final Func1<byte[], ? extends T> deserializer;

    public FactoryFlatBuffers(Func1<? super T, byte[]> serializer, Func1<byte[], ? extends T> deserializer) {
        Preconditions.checkNotNull(serializer);
        Preconditions.checkNotNull(deserializer);
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public Leaf<T, S> createLeaf(List<Entry<T, S>> entries, Context<T, S> context) {
        return new LeafFlatBuffers<T, S>(entries, context, serializer, deserializer);
    }

    @Override
    public NonLeaf<T, S> createNonLeaf(List<? extends Node<T, S>> children, Context<T, S> context) {
        return new NonLeafDefault<T, S>(children, context);
    }

    @Override
    public Entry<T, S> createEntry(T value, S geometry) {
        return Entries.entry(value, geometry);
    }

    public Func1<? super T, byte[]> serializer() {
        return serializer;
    }

    public Func1<byte[], ? extends T> deserializer() {
        return deserializer;
    }

}
