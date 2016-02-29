package com.github.davidmoten.rtree.fbs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.github.davidmoten.rtree.GreekEarthquakes;
import com.github.davidmoten.rtree.RTree;
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
        FlatBuffersSerializer serializer = new FlatBuffersSerializer();
        serializer.serialize(tree, new Func1<Object, byte[]>() {
            @Override
            public byte[] call(Object o) {
                return EMPTY;
            }
        }, os);
        os.close();
        System.out.println("written in " + (System.currentTimeMillis() - t) + "ms, " + "file size="
                + output.length() / 1000000.0 + "MB");
        System.out.println("bytes per entry=" + output.length() / tree.size());

        // InputStream is = new FileInputStream(output);
        // RTree<Object, Geometry> tr = serializer.deserialize(is);

    }

}
