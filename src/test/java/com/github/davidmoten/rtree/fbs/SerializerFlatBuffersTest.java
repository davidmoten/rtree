package com.github.davidmoten.rtree.fbs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.GreekEarthquakes;
import com.github.davidmoten.rtree.InternalStructure;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.Observable;
import rx.functions.Func1;

public class SerializerFlatBuffersTest {

    private static final byte[] EMPTY = new byte[] {};

    @Test
    public void testSerializeRoundTripToFlatBuffersSingleArray() throws Exception {
        roundTrip(InternalStructure.FLATBUFFERS_SINGLE_ARRAY, false);
    }

    @Test
    public void testSerializeRoundTripToDefaultStructure() throws Exception {
        roundTrip(InternalStructure.DEFAULT, false);
    }

    @Test
    public void testSerializeRoundTripToFlatBuffersSingleArrayBackpressure() throws Exception {
        roundTrip(InternalStructure.FLATBUFFERS_SINGLE_ARRAY, true);
    }

    @Test
    public void testSerializeRoundTripToDefaultStructureBackpressure() throws Exception {
        roundTrip(InternalStructure.DEFAULT, true);
    }

    private void roundTrip(InternalStructure structure, boolean backpressure) throws Exception {
        RTree<Object, Point> tree = RTree.star().maxChildren(10).create();
        tree = tree.add(GreekEarthquakes.entries()).last().toBlocking().single();
        long t = System.currentTimeMillis();
        File file = new File("target/file");
        FileOutputStream os = new FileOutputStream(file);
        SerializerFlatBuffers<Object, Point> fbSerializer = createSerializer();

        serialize(tree, t, file, os, fbSerializer);

        deserialize(structure, file, fbSerializer, backpressure);
    }

    private static SerializerFlatBuffers<Object, Point> createSerializer() {
        Func1<Object, byte[]> serializer = new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return EMPTY;
            }
        };
        Func1<byte[], Object> deserializer = new Func1<byte[], Object>() {
            @Override
            public Object call(byte[] bytes) {
                return null;
            }
        };
        SerializerFlatBuffers<Object, Point> fbSerializer = SerializerFlatBuffers.create(serializer,
                deserializer);
        return fbSerializer;
    }

    private static void serialize(RTree<Object, Point> tree, long t, File file, FileOutputStream os,
            SerializerFlatBuffers<Object, Point> fbSerializer) throws IOException {
        fbSerializer.serialize(tree, os);
        os.close();
        System.out.println("written in " + (System.currentTimeMillis() - t) + "ms, " + "file size="
                + file.length() / 1000000.0 + "MB");
        System.out.println("bytes per entry=" + file.length() / tree.size());
    }

    private static void deserialize(InternalStructure structure, File file,
            SerializerFlatBuffers<Object, Point> fbSerializer, boolean backpressure)
                    throws Exception {
        long t = System.currentTimeMillis();
        InputStream is = new FileInputStream(file);
        t = System.currentTimeMillis();
        RTree<Object, Point> tr = fbSerializer.deserialize(file.length(), is, structure);
        System.out.println(tr.root().get());

        System.out.println("read in " + (System.currentTimeMillis() - t) + "ms");
        Observable<Entry<Object, Point>> o = tr.search(Geometries.rectangle(40, 27.0, 40.5, 27.5));
        if (backpressure)
            o = o.take(10000);
        int found = o.count().toBlocking().single();
        System.out.println("found=" + found);
        assertEquals(22, found);
        System.out.println(tr.size());
    }

    public static void main(String[] args) throws Exception {
        // use this with jvisualvm and heap dump, find biggest objects to check
        // memory usage of rtree
        // deserialize(InternalStructure.FLATBUFFERS_SINGLE_ARRAY, new
        // File("target/file"),
        // createSerializer());
    }

}
