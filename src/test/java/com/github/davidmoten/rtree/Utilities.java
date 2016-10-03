package com.github.davidmoten.rtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;

public class Utilities {

    static List<Entry<Object, Rectangle>> entries1000() {
        List<Entry<Object, Rectangle>> list = new ArrayList<Entry<Object, Rectangle>>();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(BenchmarksRTree.class.getResourceAsStream("/1000.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                float x = Float.parseFloat(items[0]);
                float y = Float.parseFloat(items[1]);
                list.add(Entries.entry(new Object(), Geometries.rectangle(new float[]{x, y}, new float[]{x + 1, y + 1})));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
