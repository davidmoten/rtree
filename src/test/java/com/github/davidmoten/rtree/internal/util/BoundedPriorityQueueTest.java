package com.github.davidmoten.rtree.internal.util;

import static com.github.davidmoten.rtree.internal.util.BoundedPriorityQueue.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import com.github.davidmoten.guavamini.Sets;

public class BoundedPriorityQueueTest {

    private static final Comparator<Integer> comparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    };

    @Test
    public void emptyQueueAsListIsEmpty() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        assertTrue(q.asList().isEmpty());
    }

    @Test
    public void singleItemReturnsSingleItem() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        assertEquals(Sets.newHashSet(1), Sets.newHashSet(q.asList()));
    }

    @Test
    public void twoItemsReturnsSingleItemWhenMaxIsOne() {
        BoundedPriorityQueue<Integer> q = create(1, comparator);
        q.add(1);
        q.add(2);
        assertEquals(Sets.newHashSet(1), Sets.newHashSet(q.asList()));
    }

    @Test
    public void twoItemsReturnsSingleItemWhenMaxIsOneInputOrderFlipped() {
        BoundedPriorityQueue<Integer> q = create(1, comparator);
        q.add(2);
        q.add(1);
        assertEquals(Sets.newHashSet(1), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderFlipped() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(3);
        q.add(2);
        q.add(1);
        assertEquals(Sets.newHashSet(1, 2), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderIncreasing() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        q.add(2);
        q.add(3);
        assertEquals(Sets.newHashSet(1, 2), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderMixed() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(3);
        q.add(1);
        q.add(2);
        assertEquals(Sets.newHashSet(1, 2), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderMixed2() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        q.add(3);
        q.add(2);
        assertEquals(Sets.newHashSet(1, 2), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsThreeItemsWhenMaxIsOneInputOrderFlipped() {
        BoundedPriorityQueue<Integer> q = create(10, comparator);
        q.add(3);
        q.add(2);
        q.add(1);
        assertEquals(Sets.newHashSet(1, 2, 3), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsThreeItemsWhenMaxIsOneInputOrderIncreasing() {
        BoundedPriorityQueue<Integer> q = create(10, comparator);
        q.add(1);
        q.add(2);
        q.add(3);
        assertEquals(Sets.newHashSet(1, 2, 3), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsThreeItemsWhenMaxIsOneInputOrderMixed() {
        BoundedPriorityQueue<Integer> q = create(10, comparator);
        q.add(3);
        q.add(1);
        q.add(2);
        assertEquals(Sets.newHashSet(1, 2, 3), Sets.newHashSet(q.asList()));
    }

    @Test
    public void threeItemsReturnsThreeItemsWhenMaxIsOneInputOrderMixed2() {
        BoundedPriorityQueue<Integer> q = create(10, comparator);
        q.add(1);
        q.add(3);
        q.add(2);
        assertEquals(Sets.newHashSet(1, 2, 3), Sets.newHashSet(q.asList()));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInstantiateWithNegativeSizeThrowsIAE() {
        create(-1, comparator);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInstantiateWithZeroSizeThrowsIAE() {
        create(0, comparator);
    }

    @Test(expected=NullPointerException.class)
    public void testAddNullThrowsNPE() {
        BoundedPriorityQueue<Integer> q = create(10, comparator);
        q.add(null);
    }
    
}
