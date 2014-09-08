package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.rtree.geometry.Geometries;

public class Main {

    public static void main(String[] args) {
        List<Entry<Object>> entries = GreekEarthquakes.entriesList();
        int maxChildren = 128;
        RTree<Object> tree = RTree.maxChildren(maxChildren).create().add(entries);
        while (true) {
            tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).subscribe();
        }
    }
}
