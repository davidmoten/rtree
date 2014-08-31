package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Entry.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.davidmoten.rtree.geometry.Rectangle;

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
        Entry<Object> entry = entry(new Object(), r(1));
        tree = tree.add(entry);
        assertEquals(Arrays.asList(entry), tree.search(r(1)).toList().toBlocking().single());
        System.out.println("entries=" + tree.entries().toList().toBlocking().single());
    }

    @Test
    public void testPerformanceAndEntriesCount() {

        long t = System.currentTimeMillis();
        int n = 10000;
        RTree<Object> tree = createRandomRTree(n);
        long diff = System.currentTimeMillis() - t;
        System.out.println("inserts/second = " + ((double) n / diff * 1000));
        assertEquals(n, (int) tree.entries().count().toBlocking().single());

        t = System.currentTimeMillis();
        Entry<Object> entry = tree.search(rectangle(0, 0, 500, 500)).first().toBlocking().single();
        diff = System.currentTimeMillis() - t;
        System.out.println("found " + entry);
        System.out.println("time to get nearest with " + n + " entries=" + diff);

    }

    private static RTree<Object> createRandomRTree(int n) {
        RTree<Object> tree = RTree.maxChildren(4).create();
        for (int i = 0; i < n; i++) {
            Entry<Object> entry = entry(new Object(), random());
            tree = tree.add(entry);
        }
        return tree;
    }

    @Test
    public void testDeletionThatRemovesAllNodesChildren() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e(2);
        Entry<Object> e3 = e(3);
        Entry<Object> e4 = e(4);
        RTree<Object> tree = RTree.maxChildren(3).create().add(e1).add(e2).add(e3);
        assertEquals(1, tree.calculateDepth());
        tree = tree.add(e4);
        // node children are now 1,2 and 3,4
        assertEquals(2, tree.calculateDepth());
        tree = tree.delete(e4);
        // node children are now 1,2 and 3
        assertEquals(2, tree.calculateDepth());
        assertEquals(Arrays.asList(e1, e2, e3), tree.entries().toList().toBlocking().single());
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
        RTree<Object> tree = createRandomRTree(100);
        tree.visualize(600, 600, new Rectangle(-20, -20, 1100, 1100), 5).save(
                new File("target/tree.png"), "PNG");
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
    public void testDeleteOneFromLargeTree() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e(2);
        int n = 10000;
        RTree<Object> tree = createRandomRTree(n).add(e1).add(e2).delete(e1);
        assertEquals(n + 1, (int) tree.entries().count().toBlocking().single());
        assertFalse(tree.entries().contains(e1).toBlocking().single());
        assertTrue(tree.entries().contains(e2).toBlocking().single());
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

    private static Entry<Object> e(int n) {
        return entry(new Object(), r(n));
    }

    private static Rectangle r(int n) {
        return rectangle(n, n, n + 1, n + 1);
    }

    private static Rectangle r(int n, int m) {
        return rectangle(n, m, n + 1, m + 1);
    }

    private static Rectangle random() {
        return r((int) Math.round(Math.sqrt(Math.random()) * 1000),
                (int) Math.round(Math.sqrt(Math.random()) * 1000));
    }
}
