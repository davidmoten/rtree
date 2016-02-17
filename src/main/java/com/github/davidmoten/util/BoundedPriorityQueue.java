package com.github.davidmoten.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class BoundedPriorityQueue<T> {

    private final PriorityQueue<T> queue; /* backing data structure */
    private final Comparator<? super T> comparator;
    private final int maxSize;

    /**
     * Constructs a {@link BoundedPriorityQueue} with the specified
     * {@code maxSize} and {@code comparator}.
     *
     * @param maxSize
     *            - The maximum size the queue can reach, must be a positive
     *            integer.
     * @param comparator
     *            - The comparator to be used to compare the elements in the
     *            queue, must be non-null.
     */
    public BoundedPriorityQueue(final int maxSize, final Comparator<? super T> comparator) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException(
                    "maxSize = " + maxSize + "; expected a positive integer.");
        }
        if (comparator == null) {
            throw new NullPointerException("Comparator is null.");
        }
        this.queue = new PriorityQueue<T>(comparator);
        this.comparator = queue.comparator();
        this.maxSize = maxSize;
    }

    /**
     * Adds an element to the queue. If the queue contains {@code maxSize}
     * elements, {@code e} will be compared to the lowest element in the queue
     * using {@code comparator}. If {@code e} is greater than or equal to the
     * lowest element, that element will be removed and {@code e} will be added
     * instead. Otherwise, the queue will not be modified and {@code e} will not
     * be added.
     *
     * @param t
     *            - Element to be added, must be non-null.
     */
    public void add(final T t) {
        if (t == null) {
            throw new NullPointerException("e is null.");
        }
        if (maxSize <= queue.size()) {
            final T firstElement = queue.peek();
            if (comparator.compare(t, firstElement) < 1) {
                return;
            } else {
                queue.poll();
            }
        }
        queue.add(t);
    }

    /**
     * @return Returns a sorted view of the queue as a
     *         {@link Collections#unmodifiableList(java.util.List)}
     *         unmodifiableList.
     */
    public List<T> asList() {
        return Collections.unmodifiableList(new ArrayList<T>(queue));
    }
    
    public List<T> asOrderedList() {
        ArrayList<T> list = new ArrayList<T>(queue);
        Collections.sort(list, comparator);
        return list;
    }
}