package com.github.davidmoten.rtree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import rx.Subscriber;

import com.github.davidmoten.rtree.geometry.Geometries;

@State(Scope.Benchmark)
public class BenchmarksRTree {

    private final List<Entry<Object>> entries = GreekEarthquakes.entriesList();

    private final List<Entry<Object>> some = entries1000();

    private final RTree<Object> defaultTreeM4 = RTree.maxChildren(4).create().add(entries);

    private final RTree<Object> defaultTreeM10 = RTree.maxChildren(10).create().add(entries);

    private final RTree<Object> starTreeM4 = RTree.maxChildren(4).star().create().add(entries);

    private final RTree<Object> starTreeM10 = RTree.maxChildren(10).star().create().add(entries);

    private final RTree<Object> defaultTreeM32 = RTree.maxChildren(32).create().add(entries);

    private final RTree<Object> starTreeM32 = RTree.maxChildren(32).star().create().add(entries);

    private final RTree<Object> defaultTreeM128 = RTree.maxChildren(128).create().add(entries);

    private final RTree<Object> starTreeM128 = RTree.maxChildren(128).star().create().add(entries);

    private final RTree<Object> smallDefaultTreeM4 = RTree.maxChildren(4).create().add(some);

    private final RTree<Object> smallDefaultTreeM10 = RTree.maxChildren(10).create().add(some);

    private final RTree<Object> smallStarTreeM4 = RTree.maxChildren(4).star().create().add(some);

    private final RTree<Object> smallStarTreeM10 = RTree.maxChildren(10).star().create().add(some);

    private final RTree<Object> smallDefaultTreeM32 = RTree.maxChildren(32).create().add(some);

    private final RTree<Object> smallStarTreeM32 = RTree.maxChildren(32).star().create().add(some);

    private final RTree<Object> smallDefaultTreeM128 = RTree.maxChildren(128).create().add(some);

    private final RTree<Object> smallStarTreeM128 = RTree.maxChildren(128).star().create()
            .add(some);

    @Benchmark
    public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        insert(defaultTreeM4);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren004() {
        searchGreek(defaultTreeM4);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        insert(defaultTreeM10);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren010() {
        searchGreek(defaultTreeM10);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren004() {
        insert(starTreeM4);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren010() {
        insert(starTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren004() {
        searchGreek(starTreeM4);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren010() {
        searchGreek(starTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren010WithBackpressure() {
        searchGreekWithBackpressure(starTreeM10);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        insert(defaultTreeM32);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren032() {
        searchGreek(defaultTreeM32);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren032() {
        insert(starTreeM32);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren032() {
        searchGreek(starTreeM32);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        insert(defaultTreeM128);
    }

    @Benchmark
    public void defaultRTreeSearchOfGreekDataPointsMaxChildren128() {
        searchGreek(defaultTreeM128);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryIntoGreekDataEntriesMaxChildren128() {
        insert(starTreeM128);
    }

    @Benchmark
    public void rStarTreeSearchOfGreekDataPointsMaxChildren128() {
        searchGreek(starTreeM128);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        insert(smallDefaultTreeM4);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren004() {
        search(smallDefaultTreeM4);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        insert(smallDefaultTreeM10);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren010() {
        search(smallDefaultTreeM10);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren004() {
        insert(smallStarTreeM4);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren010() {
        insert(smallStarTreeM10);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren010() {
        search(smallStarTreeM10);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        insert(smallDefaultTreeM32);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren032() {
        search(smallDefaultTreeM32);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren032() {
        insert(smallStarTreeM32);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren032() {
        search(smallStarTreeM32);
    }

    @Benchmark
    public void defaultRTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        insert(smallDefaultTreeM128);
    }

    @Benchmark
    public void defaultRTreeSearchOf1000PointsMaxChildren128() {
        search(smallDefaultTreeM128);
    }

    @Benchmark
    public void rStarTreeInsertOneEntryInto1000EntriesMaxChildren128() {
        insert(smallStarTreeM128);
    }

    @Benchmark
    public void rStarTreeSearchOf1000PointsMaxChildren128() {
        search(smallStarTreeM128);
    }

    private void search(RTree<Object> tree) {
        // returns 10 results
        tree.search(Geometries.rectangle(500, 500, 630, 630)).subscribe();
    }

    private void searchGreek(RTree<Object> tree) {
        // should return 22 results
        tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).subscribe();
    }

    private void searchGreekWithBackpressure(RTree<Object> tree) {
        // should return 22 results
        tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).subscribe(new Subscriber<Object>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable arg0) {

            }

            @Override
            public void onNext(Object arg0) {
                request(1);
            }
        });
    }

    private void insert(RTree<Object> tree) {
        tree.add(new Object(), RTreeTest.random());
    }

    private static List<Entry<Object>> entries1000() {
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                BenchmarksRTree.class.getResourceAsStream("/1000.txt")));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] items = line.split(" ");
                double x = Double.parseDouble(items[0]);
                double y = Double.parseDouble(items[1]);
                list.add(Entry.entry(new Object(), Geometries.rectangle(x, y, x + 1, y + 1)));
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("list.size=" + list.size());
        return list;
    }

    public static void main(String[] args) {
        RTree<Object> tree = RTree.create().add(entries1000());
        System.out.println();
        System.out.println(tree.search(Geometries.rectangle(500, 500, 630, 630)).count()
                .toBlocking().single());

    }
}
