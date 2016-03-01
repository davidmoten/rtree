package com.github.davidmoten.rtree.fbs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.github.davidmoten.rtree.GreekEarthquakes;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import rx.functions.Func1;

public class FlatBuffersSerializerTest {

    private static final byte[] EMPTY = new byte[] {};

    @Test
    public void testSerializeRoundTrip() throws IOException {
        RTree<Object, Point> tree = RTree.star().maxChildren(10).create();
        tree = tree.add(GreekEarthquakes.entries()).last().toBlocking().single();
        long t = System.currentTimeMillis();
        File output = new File("target/file");
        FileOutputStream os = new FileOutputStream(output);
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
        FactoryFlatBuffers<Object, Point> factory = new FactoryFlatBuffers<Object, Point>(
                serializer, deserializer);
        FlatBuffersSerializer<Object, Point> fbSerializer = new FlatBuffersSerializer<Object, Point>(
                factory);
        fbSerializer.serialize(tree, os);
        os.close();
        System.out.println("written in " + (System.currentTimeMillis() - t) + "ms, " + "file size="
                + output.length() / 1000000.0 + "MB");
        System.out.println("bytes per entry=" + output.length() / tree.size());

        InputStream is = new FileInputStream(output);
        t = System.currentTimeMillis();
        RTree<Object, Point> tr = fbSerializer.deserialize(output.length(), is);
        System.out.println(tr.root().get());

        System.out.println("read in " + (System.currentTimeMillis() - t) + "ms");
        int found = tr.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).count().toBlocking()
                .single();
        System.out.println("found=" + found);
        assertEquals(22, found);
    }

}
