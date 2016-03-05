package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.github.davidmoten.rtree.fbs.Serializer;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class SerializersTest {

    @Test
    public void test() throws IOException {
        Serializer<String, Point> serializer = Serializers.flatBuffers().utf8();
        RTree<String, Point> tree = RTree.create();
        tree = tree.add("hello", Geometries.point(1, 2));
        tree = tree.add("there", Geometries.point(3, 4));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        serializer.serialize(tree, bytes);
        bytes.close();
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, Point> tree2 = serializer.deserialize(bytes.size(), input,
                    InternalStructure.DEFAULT);
            assertEquals(2, tree2.size());
        }
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, Point> tree2 = serializer.deserialize(bytes.size(), input,
                    InternalStructure.SINGLE_ARRAY);
            assertEquals(2, tree2.size());
        }
    }

}
