package com.github.davidmoten.util;

import static com.github.davidmoten.util.BoundedPriorityQueue.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

import com.github.davidmoten.guavamini.Lists;

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
        assertTrue(q.asOrderedList().isEmpty());
    }

    @Test
    public void singleItemReturnsSingleItem() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        assertEquals(Lists.newArrayList(1), q.asOrderedList());
    }

    @Test
    public void twoItemsReturnsSingleItemWhenMaxIsOne() {
        BoundedPriorityQueue<Integer> q = create(1, comparator);
        q.add(1);
        q.add(2);
        assertEquals(Lists.newArrayList(1), q.asOrderedList());
    }

    @Test
    public void twoItemsReturnsSingleItemWhenMaxIsOneInputOrderFlipped() {
        BoundedPriorityQueue<Integer> q = create(1, comparator);
        q.add(2);
        q.add(1);
        assertEquals(Lists.newArrayList(1), q.asOrderedList());
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderFlipped() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(3);
        q.add(2);
        q.add(1);
        assertEquals(Lists.newArrayList(1, 2), q.asOrderedList());
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderIncreasing() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        q.add(2);
        q.add(3);
        assertEquals(Lists.newArrayList(1, 2), q.asOrderedList());
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderMixed() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(3);
        q.add(1);
        q.add(2);
        assertEquals(Lists.newArrayList(1, 2), q.asOrderedList());
    }

    @Test
    public void threeItemsReturnsTwoItemsWhenMaxIsOneInputOrderMixed2() {
        BoundedPriorityQueue<Integer> q = create(2, comparator);
        q.add(1);
        q.add(3);
        q.add(2);
        assertEquals(Lists.newArrayList(1, 2), q.asOrderedList());
    }

}
