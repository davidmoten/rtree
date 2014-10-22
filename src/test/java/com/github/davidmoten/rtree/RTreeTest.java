package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Entry.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Functions;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RTreeTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testInstantiation() {
        RTree<Object> tree = RTree.create();
        assertTrue(tree.entries().isEmpty().toBlocking().single());
    }

    @Test
    public void testSearchEmptyTree() {
        RTree<Object> tree = RTree.create();
        assertTrue(tree.search(r(1)).isEmpty().toBlocking().single());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchOnOneItem() {
        RTree<Object> tree = RTree.create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry);
        assertEquals(Arrays.asList(entry), tree.search(r(1)).toList().toBlocking().single());
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTree<Object> tree = RTree.create().add(e(1));
        assertFalse(tree.isEmpty());
    }

    @Test
    public void testPerformanceAndEntriesCount() {

        long repeats = Long.parseLong(System.getProperty("r", "1"));
        long n = Long.parseLong(System.getProperty("n", "10000"));
        RTree<Object> tree = null;
        while (--repeats >= 0) {
            long t = System.currentTimeMillis();
            tree = createRandomRTree(n);
            long diff = System.currentTimeMillis() - t;
            System.out.println("inserts/second = " + ((double) n / diff * 1000));
        }
        assertEquals(n, (int) tree.entries().count().toBlocking().single());

        long t = System.currentTimeMillis();
        Entry<Object> entry = tree.search(rectangle(0, 0, 500, 500)).first().toBlocking().single();
        long diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " entries=" + diff);

    }

    static List<Entry<Object>> createRandomEntries(long n) {
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        for (long i = 0; i < n; i++)
            list.add(randomEntry());
        return list;
    }

    static RTree<Object> createRandomRTree(long n) {
        RTree<Object> tree = RTree.maxChildren(4).create();
        for (long i = 0; i < n; i++) {
            Entry<Object> entry = randomEntry();
            tree = tree.add(entry);
        }
        return tree;
    }

    static Entry<Object> randomEntry() {
        return entry(new Object(), random());
    }

    @Test
    public void testDepthWith0() {
        RTree<Object> tree = RTree.create();
        assertEquals(0, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries1() {
        RTree<Object> tree = create(3, 1);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries2() {
        RTree<Object> tree = create(3, 2);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries3() {
        RTree<Object> tree = create(3, 3);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries4() {
        RTree<Object> tree = create(3, 4);
        assertEquals(2, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries8() {
        RTree<Object> tree = create(3, 8);
        tree.visualize(800, 800).save(new File("target/treeLittle.png"), "PNG");
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries10() {
        RTree<Object> tree = create(3, 10);
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testSizeIsZeroIfTreeEmpty() {
        assertEquals(0, create(3, 0).size());
    }

    @Test
    public void testSizeIsOneIfTreeHasOneEntry() {
        assertEquals(1, create(3, 1).size());
    }

    @Test
    public void testSizeIsFiveIfTreeHasFiveEntries() {
        assertEquals(5, create(3, 5).size());
    }

    @Test
    public void testSizeAfterDelete() {
        Entry<Object> entry = e(1);
        RTree<Object> tree = create(3, 0).add(entry).add(entry).add(entry).delete(entry);
        assertEquals(2, tree.size());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        RTree<Object> tree = create(3, 8);
        tree = tree.add(e(10));
        // node children are now 1,2 and 3,4
        assertEquals(3, tree.calculateDepth());
        tree = tree.delete(e(10));
        // node children are now 1,2 and 3
        assertEquals(3, tree.calculateDepth());
        assertEquals(Sets.newHashSet(e(1), e(2), e(3), e(4), e(5), e(6), e(7), e(8)),
                Sets.newHashSet(tree.entries().toList().toBlocking().single()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteOfEntryThatDoesNotExistFromTreeOfOneEntry() {
        RTree<Object> tree = RTree.create().add(e(1));
        tree = tree.delete(e(2));
        assertEquals(Lists.newArrayList(e(1)), tree.entries().toList().toBlocking().single());
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTree<Object> tree = RTree.create();
        tree = tree.delete(e(2));
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testBuilder1() {
        RTree<Object> tree = RTree.minChildren(1).maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).splitter(new SplitterQuadratic())
                .create();
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTree<Object> tree = create(3, 100).delete(e(1000));
        assertEquals(100, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testBuilder2() {
        RTree<Object> tree = RTree.selector(new SelectorMinimalAreaIncrease()).minChildren(1)
                .maxChildren(4).splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTree<Object> tree = RTree.maxChildren(4).selector(new SelectorMinimalAreaIncrease())
                .minChildren(1).splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTree<Object> tree = RTree.splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).minChildren(1).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBackpressureIterationForUpTo1000Entries() {
        List<Entry<Object>> entries = Utilities.entries1000();
        RTree<Object> tree = RTree.star().create();
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(entries.get(i - 1));
            final HashSet<Entry<Object>> set = new HashSet<Entry<Object>>();
            tree.entries().subscribe(createBackpressureSubscriber(set));
            assertEquals(new HashSet<Entry<Object>>(entries.subList(0, i)), set);
        }
    }

    private static Subscriber<Entry<Object>> createBackpressureSubscriber(
            final Collection<Entry<Object>> collection) {
        return new Subscriber<Entry<Object>>() {

            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Entry<Object> t) {
                collection.add(t);
                request(1);
            }
        };
    }

    private void testBuiltTree(RTree<Object> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(i, Geometries.point(i, i));
        }
        assertEquals(1000, (int) tree.entries().count().toBlocking().single());
    }

    private static RTree<Object> create(int maxChildren, int n) {
        RTree<Object> tree = RTree.maxChildren(maxChildren).create();
        for (int i = 1; i <= n; i++)
            tree = tree.add(e(i));
        return tree;
    }

    @Test
    public void testNearest() {
        RTree<Object> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(10))
                .add(e(11));
        List<Entry<Object>> list = tree.nearest(r(9), 10, 2).toList().toBlocking().single();
        assertEquals(2, list.size());
        assertEquals(10, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(11, list.get(1).geometry().mbr().x1(), PRECISION);
    }

    @Test
    public void testVisualizer() {
        List<Entry<Object>> entries = createRandomEntries(1000);
        int maxChildren = 8;
        RTree<Object> tree = RTree.maxChildren(maxChildren).create().add(entries);
        tree.visualize(600, 600).save("target/tree.png");

        RTree<Object> tree2 = RTree.star().maxChildren(maxChildren).create().add(entries);
        tree2.visualize(600, 600).save("target/tree2.png");
    }

    @Test
    public void testVisualizerWithGreekData() {
        List<Entry<Object>> entries = GreekEarthquakes.entriesList();
        int maxChildren = 8;
        RTree<Object> tree = RTree.maxChildren(maxChildren).create().add(entries);
        tree.visualize(2000, 2000).save("target/greek.png");

        // do search
        System.out.println("found="
                + tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).count().toBlocking()
                        .single());

        RTree<Object> tree2 = RTree.maxChildren(maxChildren).star().create().add(entries);
        tree2.visualize(2000, 2000).save("target/greek2.png");
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.maxChildren(4).create().add(e1).delete(e1);
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Object> e1 = e(1);
        RTree<Object> tree = RTree.maxChildren(4).create().add(e1).add(e(2)).add(e(3)).add(e(4))
                .add(e(5)).add(e(6)).add(e(7)).add(e(8)).add(e(9)).add(e(10)).delete(e1);
        assertEquals(9, (int) tree.entries().count().toBlocking().single());
        assertFalse(tree.entries().contains(e1).toBlocking().single());
    }

    @Test
    public void testDeleteOneFromLargeTreeThenDeleteAllAndEnsureEmpty() {
        int n = 10000;
        RTree<Object> tree = createRandomRTree(n).add(e(1)).add(e(2)).delete(e(1));
        assertEquals(n + 1, (int) tree.entries().count().toBlocking().single());
        assertFalse(tree.entries().contains(e(1)).toBlocking().single());
        assertTrue(tree.entries().contains(e(2)).toBlocking().single());
        n++;
        assertEquals(n, tree.size());

        for (Entry<Object> entry : tree.entries().toBlocking().toIterable()) {
            tree = tree.delete(entry);
            n--;
            assertEquals(n, tree.size());
        }
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testDeleteOnlyDeleteOneIfThereAreMoreThanMaxChildren() {
        Entry<Object> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1).search(e1.geometry().mbr()).count().toBlocking().single();
        assertEquals(4, count);
    }

    @Test
    public void testDeleteAllIfThereAreMoreThanMaxChildren() {
        Entry<Object> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1, true).search(e1.geometry().mbr()).count().toBlocking().single();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e(2);
        RTree<Object> tree = RTree.create().add(e1);
        assertTrue(tree == tree.delete(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTree<String> tree = RTree.maxChildren(5).create();
        tree = tree.add(entry("DAVE", point(10, 20))).add(entry("FRED", point(12, 25)))
                .add(entry("MARY", point(97, 125)));
    }

    @Test(timeout = 2000)
    public void testUnsubscribe() {
        RTree<Object> tree = createRandomRTree(1000);
        assertEquals(0, (int) tree.entries().take(0).count().toBlocking().single());
    }

    @Test
    public void testSearchConditionAlwaysFalse() {
        RTree<Object> tree = create(3, 3);
        assertEquals(0, (int) tree.search(Functions.alwaysFalse()).count().toBlocking().single());
    }

    @Test
    public void testAddOverload() {
        RTree<Object> tree = create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2));
        assertEquals(1, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testDeleteOverload() {
        RTree<Object> tree = create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2)).delete(123, Geometries.point(1, 2));
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testStandardRTreeSearch() {
        Rectangle r = rectangle(13.12, 23.123, 50.45, 80.9);
        Point[] points = { point(59.0, 91.0), point(86.0, 14.0), point(36.0, 60.0),
                point(57.0, 36.0), point(14.0, 37.0) };

        RTree<UUID> tree = RTree.create();
        for (Point point : points) {
            UUID randomUUID = UUID.randomUUID();
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + randomUUID);
            tree = tree.add(randomUUID, point);
        }
        List<UUID> list = tree.search(r).map(RTreeTest.<UUID> toValue()).toList().toBlocking()
                .single();
        assertEquals(2, list.size());
    }

    @Test
    public void testStarTreeReturnsSameAsStandardRTree() {

        RTree<UUID> tree1 = RTree.create();
        RTree<UUID> tree2 = RTree.star().create();

        Rectangle[] testRects = { rectangle(13.12, 23.123, 50.45, 80.9) };
        // { rectangle(0, 0, 0, 0), rectangle(0, 0, 100, 100),
        // rectangle(0, 0, 10, 10), rectangle(0.12, 0.25, 50.356, 50.756),
        // rectangle(1, 0.252, 50, 69.23), rectangle(13.12, 23.123, 50.45,
        // 80.9),
        // rectangle(10, 10, 50, 50) };
        //

        Point[] points = { point(59.0, 91.0), point(88.0, 99.0), point(65.0, 69.0),
                point(27.0, 97.0), point(58.0, 74.0), point(2.0, 78.0), point(86.0, 14.0),
                point(36.0, 60.0), point(57.0, 36.0), point(14.0, 37.0) };

        for (Point point : points) {
            UUID randomUUID = UUID.randomUUID();
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + randomUUID);
            tree1 = tree1.add(randomUUID, point);
            tree2 = tree2.add(randomUUID, point);
        }

        for (Rectangle r : testRects) {
            Observable<Entry<UUID>> search1 = tree1.search(r);
            Observable<Entry<UUID>> search2 = tree2.search(r);
            Set<UUID> res1 = new HashSet<UUID>(search1.map(RTreeTest.<UUID> toValue()).toList()
                    .toBlocking().single());
            Set<UUID> res2 = new HashSet<UUID>(search2.map(RTreeTest.<UUID> toValue()).toList()
                    .toBlocking().single());
            System.out.println("searchRect=" + r);
            System.out.println("res1.size=" + res1.size() + ",res2.size=" + res2.size());
            System.out.println("res1=" + res1 + ",res2=" + res2);
            assertEquals(res1, res2);
        }
    }

    private static <T> Func1<Entry<T>, T> toValue() {
        return new Func1<Entry<T>, T>() {

            @Override
            public T call(Entry<T> entry) {
                return entry.value();
            }
        };
    }

    private static Point nextPoint() {

        double randomX = Math.round(Math.random() * 100);

        double randomY = Math.round(Math.random() * 100);

        return Point.create(randomX, randomY);

    }

    static Entry<Object> e(int n) {
        return Entry.<Object> entry(n, r(n));
    }

    private static Rectangle r(int n) {
        return rectangle(n, n, n + 1, n + 1);
    }

    private static Rectangle r(double n, double m) {
        return rectangle(n, m, n + 1, m + 1);
    }

    static Rectangle random() {
        return r(Math.random() * 1000, Math.random() * 1000);
    }
}
