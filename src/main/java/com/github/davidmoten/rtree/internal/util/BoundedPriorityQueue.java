package com.github.davidmoten.rtree.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;

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
        Preconditions.checkArgument(maxSize > 0, "maxSize must be > 0");
        Preconditions.checkNotNull(comparator, "comparator cannot be null");
        this.queue = new PriorityQueue<T>(reverse(comparator));
        this.comparator = comparator;
        this.maxSize = maxSize;
    }

    private static <T> Comparator<T> reverse(final Comparator<T> comparator) {
        return new Comparator<T>() {

            @Override
            public int compare(T o1, T o2) {
                return comparator.compare(o2, o1);
            }
        };
    }

    public static <T> BoundedPriorityQueue<T> create(final int maxSize,
            final Comparator<? super T> comparator) {
        return new BoundedPriorityQueue<T>(maxSize, comparator);
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
            throw new NullPointerException("cannot add null to the queue");
        }
        if (queue.size() >= maxSize) {
            final T maxElement = queue.peek();
            if (comparator.compare(maxElement, t) < 1) {
                return;
            } else {
                queue.poll();
            }
        }
        queue.add(t);
    }

    /**
     * @return Returns a view of the queue as a
     *         {@link Collections#unmodifiableList(java.util.List)}
     *         unmodifiableList sorted in reverse order.
     */
    public List<T> asList() {
        return Collections.unmodifiableList(new ArrayList<T>(queue));
    }

    public List<T> asOrderedList() {
        List<T> list = new ArrayList<T>(queue);
        Collections.sort(list, comparator);
        return list;
    }

}