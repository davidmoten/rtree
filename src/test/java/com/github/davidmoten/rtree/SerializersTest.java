package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.github.davidmoten.guavamini.Sets;
import com.github.davidmoten.rtree.fbs.Serializer;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class SerializersTest {

    @Test
    public void testStringSerialization() throws IOException {
        Serializer<String, Point> serializer = Serializers.flatBuffers().utf8();
        checkRoundTrip(serializer);
    }
    
    @Test
    public void testJavaIoSerialization() throws IOException {
        Serializer<String, Point> serializer = Serializers.flatBuffers().javaIo();
        checkRoundTrip(serializer);
    }
    

    @SuppressWarnings("unchecked")
    private static void checkRoundTrip(Serializer<String, Point> serializer) throws IOException {
        Entry<String, Point> a = Entries.entry("hello", Geometries.point(1, 2));
        Entry<String, Point> b = Entries.entry("there", Geometries.point(3, 4));
        RTree<String, Point> tree = RTree.create();
        tree = tree.add(a).add(b);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        serializer.serialize(tree, bytes);
        bytes.close();
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, Point> tree2 = serializer.deserialize(bytes.size(), input,
                    InternalStructure.DEFAULT);
            assertEquals(2, tree2.size());
            assertEquals(Sets.newHashSet(a, b),
                    Sets.newHashSet(tree2.entries().toList().toBlocking().single()));
        }
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, Point> tree2 = serializer.deserialize(bytes.size(), input,
                    InternalStructure.SINGLE_ARRAY);
            assertEquals(2, tree2.size());
            assertEquals(Sets.newHashSet(a, b),
                    Sets.newHashSet(tree2.entries().toList().toBlocking().single()));
        }        
    }

}
