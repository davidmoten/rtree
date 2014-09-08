package com.github.davidmoten.rtree;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

public class GalleryMain {

    public static void main(String[] args) {
        Observable<Entry<Object>> entries = GreekEarthquakes.entries().cache();

        List<Integer> sizes = Arrays.asList(100, 1000, 10000, 1000000);
        List<Integer> maxChildrenValues = Arrays.asList(4, 8, 16, 32, 64, 128);
        for (int size : sizes)
            for (int maxChildren : maxChildrenValues) {
                if (size > maxChildren) {
                    System.out.println("saving " + size + " m=" + maxChildren);
                    RTree<Object> tree = RTree.maxChildren(maxChildren).create()
                            .add(entries.take(size)).last().toBlocking().single();
                    tree.visualize(600, 600).save(
                            new File("target/greek-" + size + "-" + maxChildren + "-quad.png"),
                            "PNG");
                    RTree<Object> tree2 = RTree.star().maxChildren(maxChildren).create()
                            .add(entries.take(size)).last().toBlocking().single();
                    tree2.visualize(600, 600).save(
                            new File("target/greek-" + size + "-" + maxChildren + "-star.png"),
                            "PNG");
                }
            }
    }
}
