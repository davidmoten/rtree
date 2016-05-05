package com.github.davidmoten.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

public class KryoSerializationTest {

    @Test
    public void testRTree() {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Output output = new Output(bytes);
        RTree<String, Point> tree = RTree.<String, Point> create()
                .add(Entries.entry("thing", Geometries.point(10, 20)))
                .add(Entries.entry("monster", Geometries.point(23, 45)));
        kryo.writeObject(output, tree);
        output.close();
        Input input = new Input(new ByteArrayInputStream(bytes.toByteArray()));
        RTree<String, Point> tree2 = kryo.readObject(input, RTree.class);
    }

}
