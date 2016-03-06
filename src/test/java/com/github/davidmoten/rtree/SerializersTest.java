package com.github.davidmoten.rtree;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.github.davidmoten.guavamini.Sets;
import com.github.davidmoten.junit.Asserts;
import com.github.davidmoten.rtree.geometry.Circle;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Line;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class SerializersTest {

    @Test
    public void testJavaIoSerialization() throws IOException {
        Serializer<String, Point> serializer = Serializers.flatBuffers().javaIo();
        checkRoundTripPoint(serializer);
    }

    @Test
    public void testStringPointSerialization() throws IOException {
        Serializer<String, Point> serializer = Serializers.flatBuffers().utf8();
        checkRoundTripPoint(serializer);
    }

    @Test
    public void testStringRectangleSerialization() throws IOException {
        Serializer<String, Rectangle> serializer = Serializers.flatBuffers().utf8();
        Entry<String, Rectangle> a = Entries.entry("hello", Geometries.rectangle(1, 2, 3, 4));
        Entry<String, Rectangle> b = Entries.entry("there", Geometries.rectangle(3, 4, 5, 6));
        check(serializer, a, b);
    }

    @Test
    public void testStringCircleSerialization() throws IOException {
        Serializer<String, Circle> serializer = Serializers.flatBuffers().utf8();
        Entry<String, Circle> a = Entries.entry("hello", Geometries.circle(1, 2, 3));
        Entry<String, Circle> b = Entries.entry("there", Geometries.circle(3, 4, 5));
        check(serializer, a, b);
    }

    @Test
    public void testStringLineSerialization() throws IOException {
        Serializer<String, Line> serializer = Serializers.flatBuffers().utf8();
        Entry<String, Line> a = Entries.entry("hello", Geometries.line(1, 2, 3, 4));
        Entry<String, Line> b = Entries.entry("there", Geometries.line(3, 4, 5, 6));
        check(serializer, a, b);
    }

    private static void checkRoundTripPoint(Serializer<String, Point> serializer)
            throws IOException {
        Entry<String, Point> a = Entries.entry("hello", Geometries.point(1, 2));
        Entry<String, Point> b = Entries.entry("there", Geometries.point(3, 4));
        check(serializer, a, b);
    }

    @SuppressWarnings("unchecked")
    private static <S extends Geometry> void check(Serializer<String, S> serializer,
            Entry<String, S> a, Entry<String, S> b) throws IOException {
        RTree<String, S> tree = RTree.create();
        tree = tree.add(a).add(b);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        serializer.write(tree, bytes);
        bytes.close();
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, S> tree2 = serializer.read( input,bytes.size(),
                    InternalStructure.DEFAULT);
            assertEquals(2, tree2.size());
            assertEquals(Sets.newHashSet(a, b),
                    Sets.newHashSet(tree2.entries().toList().toBlocking().single()));
        }
        {
            ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
            RTree<String, S> tree2 = serializer.read( input,bytes.size(),
                    InternalStructure.SINGLE_ARRAY);
            assertEquals(2, tree2.size());
            assertEquals(Sets.newHashSet(a, b),
                    Sets.newHashSet(tree2.entries().toList().toBlocking().single()));
        }
    }
    
    @Test
    public void isUtilityClass() {
        Asserts.assertIsUtilityClass(Serializers.class);
    }

}
