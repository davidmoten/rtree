package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Entry.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Functions;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class RTreeTest {

    private static final double PRECISION = 0.000001;

    @Test
    public void testInstantiation() {
        RTree<Object, Geometry> tree = RTree.create();
        assertTrue(tree.entries().isEmpty().toBlocking().single());
    }

    @Test
    public void testSearchEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        assertTrue(tree.search(r(1)).isEmpty().toBlocking().single());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSearchOnOneItem() {
        RTree<Object, Geometry> tree = RTree.create();
        Entry<Object, Geometry> entry = e(1);
        tree = tree.add(entry);
        assertEquals(Arrays.asList(entry), tree.search(r(1)).toList().toBlocking().single());
    }

    @Test
    public void testTreeWithOneItemIsNotEmpty() {
        RTree<Object, Geometry> tree = RTree.create().add(e(1));
        assertFalse(tree.isEmpty());
    }

    // @Test(expected = IOException.class)
    public void testSaveFileException() throws IOException {
        FileLock lock = null;
        RandomAccessFile file = null;
        try {
            String filename = "target/locked.png";
            File f = new File(filename);
            f.createNewFile();
            file = new RandomAccessFile(f, "rw");
            lock = file.getChannel().lock();
            RTree.create().visualize(600, 600).save(filename, "PNG");
        } finally {
            try {
                lock.release();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testVisualizerWithEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        tree.visualize(600, 600).save("target/tree.png", "PNG");
    }

    @Test
    public void testAddObservable() {
        Entry<Object, Geometry> e1 = e(1);
        Entry<Object, Geometry> e2 = e2(1);

        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e1).add(e2).delete(e1);
        RTree<Object, Geometry> emptyTree = RTree.maxChildren(4).create();
        rx.Observable<?> deletedtree = emptyTree.add(tree.entries());
        assertEquals(2, (int) deletedtree.count().toBlocking().single());
    }

    @Test
    public void testPerformanceAndEntriesCount() {

        long repeats = Long.parseLong(System.getProperty("r", "1"));
        long n = Long.parseLong(System.getProperty("n", "10000"));
        RTree<Object, Geometry> tree = null;
        while (--repeats >= 0) {
            long t = System.currentTimeMillis();
            tree = createRandomRTree(n);
            long diff = System.currentTimeMillis() - t;
            System.out.println("inserts/second = " + ((double) n / diff * 1000));
        }
        assertEquals(n, (int) tree.entries().count().toBlocking().single());

        long t = System.currentTimeMillis();
        Entry<Object, Geometry> entry = tree.search(rectangle(0, 0, 500, 500)).first().toBlocking()
                .single();
        long diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " entries=" + diff);

    }

    @Test
    public void testSearchOfPoint() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = tree.search(point(1, 1)).toList().toBlocking()
                .single();
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    @Test
    public void testSearchOfPointWithinDistance() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = tree.search(point(1, 1), 2).toList().toBlocking()
                .single();
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    static List<Entry<Object, Geometry>> createRandomEntries(long n) {
        List<Entry<Object, Geometry>> list = new ArrayList<Entry<Object, Geometry>>();
        for (long i = 0; i < n; i++)
            list.add(randomEntry());
        return list;
    }

    static RTree<Object, Geometry> createRandomRTree(long n) {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create();
        for (long i = 0; i < n; i++) {
            Entry<Object, Geometry> entry = randomEntry();
            tree = tree.add(entry);
        }
        return tree;
    }

    static Entry<Object, Geometry> randomEntry() {
        return entry(new Object(), (Geometry) random());
    }

    @Test
    public void testDeleteWithGeometry() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create();
        Entry<Object, Geometry> entry = e(1);
        Entry<Object, Geometry> entry2 = e2(1);
        tree = tree.add(entry).add(entry2);

        tree = tree.delete(entry.value(), entry.geometry(), true);
        List<Entry<Object, Geometry>> entries = tree.entries().toList().toBlocking().single();
        assertTrue(entries.contains(entry2) && !entries.contains(entry));
    }

    @Test
    public void testDepthWith0() {
        RTree<Object, Geometry> tree = RTree.create();
        tree = tree.add(createRandomEntries(5));
        List<Entry<Object, Geometry>> entries = tree.entries().toList().toBlocking().single();
        RTree<Object, Geometry> deletedTree = tree.delete(entries, true);
        assertTrue(deletedTree.isEmpty());
    }

    @Test
    public void testContext() {
        RTree<Object, Geometry> tree = RTree.create();
        assertNotNull(tree.context());
    }

    @Test
    public void testIterableDeletion() {
        RTree<Object, Geometry> tree = RTree.create();
        Entry<Object, Geometry> entry1 = e(1);
        Entry<Object, Geometry> entry2 = e(2);
        Entry<Object, Geometry> entry3 = e(3);
        tree = tree.add(entry1).add(entry2).add(entry3);

        List<Entry<Object, Geometry>> list = new ArrayList<Entry<Object, Geometry>>();
        list.add(entry1);
        list.add(entry3);
        RTree<Object, Geometry> deletedTree = tree.delete(list);
        List<Entry<Object, Geometry>> entries = deletedTree.entries().toList().toBlocking()
                .single();
        assertTrue(entries.contains(entry2) && !entries.contains(entry1)
                && !entries.contains(entry3));
    }

    @Test
    public void testObservableDeletion() {
        RTree<Object, Geometry> tree = RTree.create();
        Entry<Object, Geometry> entry1 = e(1);
        Entry<Object, Geometry> entry2 = e(3);
        Entry<Object, Geometry> entry3 = e(5);
        tree = tree.add(entry1).add(entry2).add(entry3);
        rx.Observable<Entry<Object, Geometry>> obs = tree.search(r(2), 5);
        rx.Observable<RTree<Object, Geometry>> deleted = tree.delete(obs, true);
        assertTrue(deleted.elementAt(deleted.count().toBlocking().single() - 1).count()
                .toBlocking().single() == 1);
    }

    @Test
    public void testFullDeletion() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create();
        Entry<Object, Geometry> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, true);
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testPartialDeletion() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create();
        Entry<Object, Geometry> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, false);
        List<Entry<Object, Geometry>> entries = tree.entries().toList().toBlocking().single();
        int countEntries = tree.entries().count().toBlocking().single();
        assertTrue(countEntries == 1);
        assertTrue(entries.get(0).equals(entry));
    }

    @Test
    public void testDepthWithMaxChildren3Entries1() {
        RTree<Object, Geometry> tree = create(3, 1);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries2() {
        RTree<Object, Geometry> tree = create(3, 2);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries3() {
        RTree<Object, Geometry> tree = create(3, 3);
        assertEquals(1, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries4() {
        RTree<Object, Geometry> tree = create(3, 4);
        assertEquals(2, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries8() {
        RTree<Object, Geometry> tree = create(3, 8);
        tree.visualize(800, 800).save(new File("target/treeLittle.png"), "PNG");
        assertEquals(3, tree.calculateDepth());
    }

    @Test
    public void testDepthWithMaxChildren3Entries10() {
        RTree<Object, Geometry> tree = create(3, 10);
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
        Entry<Object, Geometry> entry = e(1);
        RTree<Object, Geometry> tree = create(3, 0).add(entry).add(entry).add(entry).delete(entry);
        assertEquals(2, tree.size());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        RTree<Object, Geometry> tree = create(3, 8);
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
        RTree<Object, Geometry> tree = RTree.create().add(e(1));
        tree = tree.delete(e(2));
        assertEquals(Lists.newArrayList(e(1)), tree.entries().toList().toBlocking().single());
    }

    @Test
    public void testDeleteFromEmptyTree() {
        RTree<Object, Geometry> tree = RTree.create();
        tree = tree.delete(e(2));
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testBuilder1() {
        RTree<Object, Geometry> tree = RTree.minChildren(1).maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).splitter(new SplitterQuadratic())
                .create();
        testBuiltTree(tree);
    }

    @Test
    public void testDeletionOfEntryThatDoesNotExistFromNonLeaf() {
        RTree<Object, Geometry> tree = create(3, 100).delete(e(1000));
        assertEquals(100, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testBuilder2() {
        RTree<Object, Geometry> tree = RTree.selector(new SelectorMinimalAreaIncrease())
                .minChildren(1).maxChildren(4).splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder3() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).minChildren(1)
                .splitter(new SplitterQuadratic()).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBuilder4() {
        RTree<Object, Geometry> tree = RTree.splitter(new SplitterQuadratic()).maxChildren(4)
                .selector(new SelectorMinimalAreaIncrease()).minChildren(1).create();
        testBuiltTree(tree);
    }

    @Test
    public void testBackpressureIterationForUpTo1000Entries() {
        List<Entry<Object, Rectangle>> entries = Utilities.entries1000();
        RTree<Object, Geometry> tree = RTree.star().create();
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(entries.get(i - 1));
            final HashSet<Entry<Object, Geometry>> set = new HashSet<Entry<Object, Geometry>>();
            tree.entries().subscribe(createBackpressureSubscriber(set));
            assertEquals(new HashSet<Entry<Object, Rectangle>>(entries.subList(0, i)), set);
        }
    }

    private static Subscriber<Entry<Object, Geometry>> createBackpressureSubscriber(
            final Collection<Entry<Object, Geometry>> collection) {
        return new Subscriber<Entry<Object, Geometry>>() {

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
            public void onNext(Entry<Object, Geometry> t) {
                collection.add(t);
                request(1);
            }
        };
    }

    private void testBuiltTree(RTree<Object, Geometry> tree) {
        for (int i = 1; i <= 1000; i++) {
            tree = tree.add(i, Geometries.point(i, i));
        }
        assertEquals(1000, (int) tree.entries().count().toBlocking().single());
    }

    private static RTree<Object, Geometry> create(int maxChildren, int n) {
        RTree<Object, Geometry> tree = RTree.maxChildren(maxChildren).create();
        for (int i = 1; i <= n; i++)
            tree = tree.add(e(i));
        return tree;
    }

    @Test
    public void testNearestSameDirection() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(3))
                .add(e(10)).add(e(11));
        List<Entry<Object, Geometry>> list = tree.nearest(r(9), 10, 2).toList().toBlocking()
                .single();
        assertEquals(2, list.size());
        assertEquals(10, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(11, list.get(1).geometry().mbr().x1(), PRECISION);

        List<Entry<Object, Geometry>> list2 = tree.nearest(r(10), 8, 3).toList().toBlocking()
                .single();
        assertEquals(2, list2.size());
        assertEquals(11, list2.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(10, list2.get(1).geometry().mbr().x1(), PRECISION);
    }

    @Test
    public void testNearestDifferentDirections() {
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(3))
                .add(e(9)).add(e(10));
        List<Entry<Object, Geometry>> list = tree.nearest(r(6), 10, 2).toList().toBlocking()
                .single();
        assertEquals(2, list.size());
        assertEquals(3, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(9, list.get(1).geometry().mbr().x1(), PRECISION);
    }

    @Test
    public void testNearestToAPoint() {
        Object value = new Object();
        RTree<Object, Geometry> tree = RTree.create().add(value, point(1, 1));
        List<Entry<Object, Geometry>> list = tree.nearest(point(2, 2), 3, 2).toList().toBlocking()
                .single();
        assertEquals(1, list.size());
        assertEquals(value, list.get(0).value());
    }

    @Test
    public void testVisualizer() {
        List<Entry<Object, Geometry>> entries = createRandomEntries(1000);
        int maxChildren = 8;
        RTree<Object, Geometry> tree = RTree.maxChildren(maxChildren).create().add(entries);
        tree.visualize(600, 600).save("target/tree.png");

        RTree<Object, Geometry> tree2 = RTree.star().maxChildren(maxChildren).create().add(entries);
        tree2.visualize(600, 600).save("target/tree2.png");
    }

    @Test(expected = RuntimeException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList() {
        SplitterRStar spl = new SplitterRStar();
        spl.split(Collections.<HasGeometry> emptyList(), 4);
    }

    @Test
    public void testVisualizerWithGreekData() {
        List<Entry<Object, Point>> entries = GreekEarthquakes.entriesList();
        int maxChildren = 8;
        RTree<Object, Point> tree = RTree.maxChildren(maxChildren).<Object, Point> create()
                .add(entries);
        tree.visualize(2000, 2000).save("target/greek.png");

        // do search
        System.out.println("found="
                + tree.search(Geometries.rectangle(40, 27.0, 40.5, 27.5)).count().toBlocking()
                        .single());

        RTree<Object, Point> tree2 = RTree.maxChildren(maxChildren).star().<Object, Point> create()
                .add(entries);
        tree2.visualize(2000, 2000).save("target/greek2.png");
    }

    @Test
    public void testDeleteOneFromOne() {
        Entry<Object, Geometry> e1 = e(1);
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e1).delete(e1);
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testDeleteOneFromTreeWithDepthGreaterThanOne() {
        Entry<Object, Geometry> e1 = e(1);
        RTree<Object, Geometry> tree = RTree.maxChildren(4).create().add(e1).add(e(2)).add(e(3))
                .add(e(4)).add(e(5)).add(e(6)).add(e(7)).add(e(8)).add(e(9)).add(e(10)).delete(e1);
        assertEquals(9, (int) tree.entries().count().toBlocking().single());
        assertFalse(tree.entries().contains(e1).toBlocking().single());
    }

    @Test
    public void testDeleteOneFromLargeTreeThenDeleteAllAndEnsureEmpty() {
        int n = 10000;
        RTree<Object, Geometry> tree = createRandomRTree(n).add(e(1)).add(e(2)).delete(e(1));
        assertEquals(n + 1, (int) tree.entries().count().toBlocking().single());
        assertFalse(tree.entries().contains(e(1)).toBlocking().single());
        assertTrue(tree.entries().contains(e(2)).toBlocking().single());
        n++;
        assertEquals(n, tree.size());

        for (Entry<Object, Geometry> entry : tree.entries().toBlocking().toIterable()) {
            tree = tree.delete(entry);
            n--;
            assertEquals(n, tree.size());
        }
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testDeleteOnlyDeleteOneIfThereAreMoreThanMaxChildren() {
        Entry<Object, Geometry> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1).search(e1.geometry().mbr()).count().toBlocking().single();
        assertEquals(4, count);
    }

    @Test
    public void testDeleteAllIfThereAreMoreThanMaxChildren() {
        Entry<Object, Geometry> e1 = e(1);
        int count = RTree.maxChildren(4).create().add(e1).add(e1).add(e1).add(e1).add(e1)
                .delete(e1, true).search(e1.geometry().mbr()).count().toBlocking().single();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteItemThatIsNotPresentDoesNothing() {
        Entry<Object, Geometry> e1 = e(1);
        Entry<Object, Geometry> e2 = e(2);
        RTree<Object, Geometry> tree = RTree.create().add(e1);
        assertTrue(tree == tree.delete(e2));
    }

    @Test
    public void testExampleOnReadMe() {
        RTree<String, Geometry> tree = RTree.maxChildren(5).create();
        tree = tree.add(entry("DAVE", point(10, 20))).add(entry("FRED", point(12, 25)))
                .add(entry("MARY", point(97, 125)));
    }

    @Test(timeout = 2000)
    public void testUnsubscribe() {
        RTree<Object, Geometry> tree = createRandomRTree(1000);
        assertEquals(0, (int) tree.entries().take(0).count().toBlocking().single());
    }

    @Test
    public void testSearchConditionAlwaysFalse() {
        RTree<Object, Geometry> tree = create(3, 3);
        assertEquals(0, (int) tree.search(Functions.alwaysFalse()).count().toBlocking().single());
    }

    @Test
    public void testAddOverload() {
        RTree<Object, Geometry> tree = create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2));
        assertEquals(1, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testDeleteOverload() {
        RTree<Object, Geometry> tree = create(3, 0);
        tree = tree.add(123, Geometries.point(1, 2)).delete(123, Geometries.point(1, 2));
        assertEquals(0, (int) tree.entries().count().toBlocking().single());
    }

    @Test
    public void testStandardRTreeSearch() {
        Rectangle r = rectangle(13.0, 23.0, 50.0, 80.0);
        Point[] points = { point(59.0, 91.0), point(86.0, 14.0), point(36.0, 60.0),
                point(57.0, 36.0), point(14.0, 37.0) };

        RTree<Integer, Geometry> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = new HashSet<Integer>(tree.search(r).map(RTreeTest.<Integer> toValue())
                .toList().toBlocking().single());
        assertEquals(new HashSet<Integer>(asList(3, 5)), set);
    }

    @Test
    public void testStandardRTreeSearch2() {
        Rectangle r = rectangle(10.0, 10.0, 50.0, 50.0);
        Point[] points = { point(28.0, 19.0), point(29.0, 4.0), point(10.0, 63.0),
                point(34.0, 85.0), point(62.0, 45.0) };

        RTree<Integer, Geometry> tree = RTree.create();
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            System.out.println("point(" + point.x() + "," + point.y() + "), value=" + (i + 1));
            tree = tree.add(i + 1, point);
        }
        System.out.println(tree.asString());
        System.out.println("searching " + r);
        Set<Integer> set = new HashSet<Integer>(tree.search(r).map(RTreeTest.<Integer> toValue())
                .toList().toBlocking().single());
        assertEquals(new HashSet<Integer>(asList(1)), set);
    }

    @Test
    public void testStarTreeReturnsSameAsStandardRTree() {

        RTree<Integer, Geometry> tree1 = RTree.create();
        RTree<Integer, Geometry> tree2 = RTree.star().create();

        Rectangle[] testRects = { rectangle(0, 0, 0, 0), rectangle(0, 0, 100, 100),
                rectangle(0, 0, 10, 10), rectangle(0.12, 0.25, 50.356, 50.756),
                rectangle(1, 0.252, 50, 69.23), rectangle(13.12, 23.123, 50.45, 80.9),
                rectangle(10, 10, 50, 50) };

        for (int i = 1; i <= 10000; i++) {
            Point point = nextPoint();
            // System.out.println("point(" + point.x() + "," + point.y() +
            // "),");
            tree1 = tree1.add(i, point);
            tree2 = tree2.add(i, point);
        }

        for (Rectangle r : testRects) {
            Set<Integer> res1 = new HashSet<Integer>(tree1.search(r)
                    .map(RTreeTest.<Integer> toValue()).toList().toBlocking().single());
            Set<Integer> res2 = new HashSet<Integer>(tree2.search(r)
                    .map(RTreeTest.<Integer> toValue()).toList().toBlocking().single());
            // System.out.println("searchRect= rectangle(" + r.x1() + "," +
            // r.y1() + "," + r.x2() + "," + r.y2()+ ")");
            // System.out.println("res1.size=" + res1.size() + ",res2.size=" +
            // res2.size());
            // System.out.println("res1=" + res1 + ",res2=" + res2);
            assertEquals(res1.size(), res2.size());
        }
    }

    private static <T> Func1<Entry<T, ?>, T> toValue() {
        return new Func1<Entry<T, ?>, T>() {

            @Override
            public T call(Entry<T, ?> entry) {
                return entry.value();
            }
        };
    }

    private static Point nextPoint() {

        double randomX = Math.round(Math.random() * 100);

        double randomY = Math.round(Math.random() * 100);

        return Point.create(randomX, randomY);

    }

    static Entry<Object, Geometry> e(int n) {
        return Entry.<Object, Geometry> entry(n, r(n));
    }

    static Entry<Object, Geometry> e2(int n) {
        return Entry.<Object, Geometry> entry(n, r(n - 1));
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
