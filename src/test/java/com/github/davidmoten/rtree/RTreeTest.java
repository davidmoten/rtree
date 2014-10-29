package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.Entry.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.point;
import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

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

import org.junit.Assert;
import org.junit.Test;

import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.Subscription;
import rx.functions.Functions;
import rx.Observable.*;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.HasGeometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rx.operators.OperatorBoundedPriorityQueue;
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
    public void testSaveFileException(){
        List<Entry<Object>> entries = createRandomEntries(1000);
        int maxChildren = 8;
        RTree<Object> tree = new RTree<Object>(null);
        FileLock lock = null;
        RandomAccessFile file = null;
        try
        {
        	file = new RandomAccessFile("target/tree.png", "rw");
        	lock = file.getChannel().lock();
        	tree.visualize(600, 600).save("target/tree.png", "PNG");
        }
        catch(IOException exception)
        {
        	exception.printStackTrace();
        }
        catch (RuntimeException e)
        {
        	try
        	{
        		lock.release();
        		file.close();
        		assertTrue(true);
        		return;
        	}
        	catch(Exception e1)
        	{
        		e.printStackTrace();
        	}
        }
        Assert.fail();
    }
  
    @Test
    public void testVisualizerAbsent() {
        List<Entry<Object>> entries = createRandomEntries(1000);
        int maxChildren = 8;
        RTree<Object> tree = new RTree<Object>(null);
        tree.visualize(600, 600).save("target/tree.png", "PNG");

    }
    
    @Test
    public void testAddObservable() {
        Entry<Object> e1 = e(1);
        Entry<Object> e2 = e2(1);
        
        RTree<Object> tree = RTree.maxChildren(4).create().add(e1).add(e2).delete(e1);
        RTree<Object> emptyTree = RTree.maxChildren(4).create();
        rx.Observable<?> deletedtree = emptyTree.add(tree.entries());
        assertEquals(2, (int) deletedtree.count().toBlocking().single());
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
    public void testDeleteWithGeometry() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        Entry<Object> entry2 = e2(1);
        tree = tree.add(entry).add(entry2);

        tree = tree.delete(entry.value(), entry.geometry(), true);
        List<Entry<Object>> entries = tree.entries().toList().toBlocking().single();
        assertTrue(entries.contains(entry2) && !entries.contains(entry) );
    }
    
    @Test
    public void testDepthWith0() {
        RTree<Object> tree = RTree.create();
        tree = tree.add(createRandomEntries(5));
        List<Entry<Object>> entries = tree.entries().toList().toBlocking().single();
        RTree<Object> deletedTree = tree.delete(entries, true);
        assertTrue(deletedTree.isEmpty());
    }
    
    @Test
    public void testContext()
    {
    	RTree<Object> tree = RTree.create();
        assertNotNull(tree.context());
    }
 
    @Test
    public void testIterableDeletion() {
        RTree<Object> tree = RTree.create();
        Entry<Object> entry1 = e(1);
        Entry<Object> entry2 = e(2);
        Entry<Object> entry3 = e(3);
        tree = tree.add(entry1).add(entry2).add(entry3);
        
        List<Entry<Object>> list = new ArrayList<Entry<Object>>();
        list.add(entry1);
        list.add(entry3);
        RTree<Object> deletedTree = tree.delete(list);
        List<Entry<Object>> entries = deletedTree.entries().toList().toBlocking().single();
        assertTrue(entries.contains(entry2) && !entries.contains(entry1) && !entries.contains(entry3));
    }
    
    @Test
    public void testObservableDeletion() {
        RTree<Object> tree = RTree.create();
        Entry<Object> entry1 = e(1);
        Entry<Object> entry2 = e(3);
        Entry<Object> entry3 = e(5);
        tree = tree.add(entry1).add(entry2).add(entry3);
        rx.Observable<Entry<Object>> obs = tree.search(r(2), 5);
        rx.Observable<RTree<Object>> deleted = tree.delete(obs, true);       
        assertTrue(deleted.elementAt(deleted.count().toBlocking().single()-1).count().toBlocking().single() == 1);
    }
    
    @Test
    public void testFullDeletion() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, true);
        assertTrue(tree.isEmpty());
    }
    
    @Test
    public void testPartialDeletion() {
        RTree<Object> tree = RTree.maxChildren(4).create();
        Entry<Object> entry = e(1);
        tree = tree.add(entry).add(entry);
        tree = tree.delete(entry, false);
        List<Entry<Object>> entries = tree.entries().toList().toBlocking().single();
        int countEntries =  tree.entries().count().toBlocking().single();
        assertTrue(countEntries == 1);
        assertTrue(entries.get(0).equals(entry));
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
    public void testNearestSameDirection() {
        RTree<Object> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(3)).add(e(10))
                .add(e(11));
        List<Entry<Object>> list = tree.nearest(r(9), 10, 2).toList().toBlocking().single();
        assertEquals(2, list.size());
        assertEquals(10, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(11, list.get(1).geometry().mbr().x1(), PRECISION);
        
        List<Entry<Object>> list2 = tree.nearest(r(10), 8, 3).toList().toBlocking().single();
        assertEquals(2, list2.size());
        assertEquals(11, list2.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(10, list2.get(1).geometry().mbr().x1(), PRECISION);
    }
    
    @Test
    public void testNearestDifferentDirections() {
        RTree<Object> tree = RTree.maxChildren(4).create().add(e(1)).add(e(2)).add(e(3)).add(e(9))
                .add(e(10));
        List<Entry<Object>> list = tree.nearest(r(6), 10, 2).toList().toBlocking().single();
        assertEquals(2, list.size());
        assertEquals(3, list.get(0).geometry().mbr().x1(), PRECISION);
        assertEquals(9, list.get(1).geometry().mbr().x1(), PRECISION);
        
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
    
    @Test(expected = RuntimeException.class)
    public void testSplitterRStarThrowsExceptionOnEmptyList(){
    	SplitterRStar spl = new SplitterRStar();
    	spl.split(Collections.<HasGeometry> emptyList(), 4);
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
        Rectangle r = rectangle(13.0, 23.0, 50.0, 80.0);
        Point[] points = { point(59.0, 91.0), point(86.0, 14.0), point(36.0, 60.0),
                point(57.0, 36.0), point(14.0, 37.0) };

        RTree<Integer> tree = RTree.create();
        for (int i 
        = 0; i < points.length; i++) {
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
        Rectangle r = rectangle(10.0,10.0,50.0,50.0);
        Point[] points = { point(28.0,19.0),
                point(29.0,4.0),
                point(10.0,63.0),
                point(34.0,85.0),
                point(62.0,45.0) };

        RTree<Integer> tree = RTree.create();
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

        RTree<Integer> tree1 = RTree.create();
        RTree<Integer> tree2 = RTree.star().create();

        Rectangle[] testRects = { rectangle(0, 0, 0, 0), rectangle(0, 0, 100, 100),
                rectangle(0, 0, 10, 10), rectangle(0.12, 0.25, 50.356, 50.756),
                rectangle(1, 0.252, 50, 69.23), rectangle(13.12, 23.123, 50.45, 80.9),
                rectangle(10, 10, 50, 50) };

        for (int i = 1; i <= 10000; i++) {
            Point point = nextPoint();
//            System.out.println("point(" + point.x() + "," + point.y() + "),");
            tree1 = tree1.add(i, point);
            tree2 = tree2.add(i, point);
        }

        for (Rectangle r : testRects) {
            Set<Integer> res1 = new HashSet<Integer>(tree1.search(r).map(RTreeTest.<Integer> toValue()).toList()
                    .toBlocking().single());
            Set<Integer> res2 = new HashSet<Integer>(tree2.search(r).map(RTreeTest.<Integer> toValue()).toList()
                    .toBlocking().single());
//            System.out.println("searchRect= rectangle(" + r.x1() + "," + r.y1() + "," + r.x2() + "," + r.y2()+ ")");
//            System.out.println("res1.size=" + res1.size() + ",res2.size=" + res2.size());
//            System.out.println("res1=" + res1 + ",res2=" + res2);
            assertEquals(res1.size(), res2.size());
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
    
    static Entry<Object> e2(int n) {
        return Entry.<Object> entry(n, r(n-1));
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
