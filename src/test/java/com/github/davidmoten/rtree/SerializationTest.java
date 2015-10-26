package com.github.davidmoten.rtree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.davidmoten.rtree.geometry.Point;

public class SerializationTest {
    
    @Test
    public void testSerializationRoundTrip() throws FileNotFoundException {
        List<Entry<Object, Point>> entries = GreekEarthquakes.entriesList();
        int maxChildren = 8;
        RTree<Object, Point> tree = RTree.maxChildren(maxChildren).<Object, Point> create()
                .add(entries);
        
        File file  = new File("target/greek-serialized.kryo");
        file.delete();
        
        Kryo kryo = new Kryo();
        Output output = new Output(new FileOutputStream(file));
        kryo.writeObject(output, tree);
        output.close();
        
        Input input = new Input(new FileInputStream(file));
//        RTree<Object,Point> tree2 = kryo.readObject(input, RTree.class);
        
        
    }

}
