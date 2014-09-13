package com.github.davidmoten.rtree;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometries;

public class Main {

    public static void main(String[] args) {

        ArrayList list = new ArrayList<Integer>(2);
        list.add(1); // size() == 1
        list.add(2); // size() == 2, list is "filled"

        for (int m = 4; m <= 256; m++) {
            int n = 38000;
            double q = Math.ceil(Math.log(n) / Math.log(m));
            double order = n / Math.pow(m, q) * m * q;
            System.out.println("m=" + m + ", order=" + order);
        }

        List<Entry<Object>> entries = GreekEarthquakes.entriesList();
        int maxChildren = 10;
        RTree<Object> tree = RTree.maxChildren(maxChildren).create().add(entries);
        while (true) {
            // tree.search(Geometries.rectangle(40, 27.0, 40.5,
            // 27.5)).subscribe();
            tree.add(new Object(), Geometries.point(40, 27));
        }
    }
}
