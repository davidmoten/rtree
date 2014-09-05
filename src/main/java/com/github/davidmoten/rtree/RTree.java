package com.github.davidmoten.rtree;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.util.Comparator;

import rx.Observable;
import rx.functions.Func1;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rx.operators.OperatorBoundedPriorityQueue;
import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 * 
 * @param <R>
 *            the entry type
 */
public final class RTree<R> {

    private final ImmutableStack<NonLeaf<R>> emptyStack = ImmutableStack.<NonLeaf<R>> empty();

    private final Optional<Node<R>> root;
    private final Context context;

    public static final int MAX_CHILDREN_DEFAULT = 32;

    private int size;

    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the tree if present
     * @param context
     *            options for the R-tree
     */
    private RTree(Optional<Node<R>> root, int size, Context context) {
        this.root = root;
        this.size = size;
        this.context = context;
    }

    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the R-tree
     * @param context
     *            options for the R-tree
     */
    private RTree(Node<R> root, int size, Context context) {
        this(of(root), size, context);
    }

    private RTree(Context context) {
        this(Optional.<Node<R>> absent(), 0, context);
    }

    /**
     * Returns a new Builder instance for {@link RTree}. Defaults to
     * maxChildren=128, minChildren=64, splitter=QuadraticSplitter.
     * 
     * @return a new RTree instance
     */
    public static <T> RTree<T> create() {
        return new Builder().create();
    }

    public int calculateDepth() {
        return calculateDepth(root);
    }

    private static <R> int calculateDepth(Optional<Node<R>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <R> int calculateDepth(Node<R> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<R>) node).children().get(0), depth + 1);
    }

    /**
     * When the number of children in an R-tree node drops below this number the
     * node is deleted and the children are added on to the R-tree again.
     * 
     * @param minChildren
     *            less than this number of children in a node triggers a node
     *            deletion and redistribution of its members
     * @return builder
     */
    public static Builder minChildren(int minChildren) {
        return new Builder().minChildren(minChildren);
    }

    /**
     * Sets the max number of children in an R-tree node.
     * 
     * @param maxChildren
     *            max number of children in an R-tree node
     * @return builder
     */
    public static Builder maxChildren(int maxChildren) {
        return new Builder().maxChildren(maxChildren);
    }

    /**
     * Sets the {@link Splitter} to use when maxChildren is reached.
     * 
     * @param splitter
     *            the splitter algorithm to use
     * @return builder
     */
    public static Builder splitter(Splitter splitter) {
        return new Builder().splitter(splitter);
    }

    public static Builder selector(Selector selector) {
        return new Builder().selector(selector);
    }

    /**
     * RTree Builder
     */
    public static class Builder {

        private int maxChildren = MAX_CHILDREN_DEFAULT;
        private Optional<Integer> minChildren = absent();
        private Splitter splitter = new SplitterQuadratic();
        private Selector selector = new SelectorMinimalAreaIncrease();

        private Builder() {
        }

        /**
         * When the number of children in an R-tree node drops below this number
         * the node is deleted and the children are added on to the R-tree
         * again.
         * 
         * @param minChildren
         *            less than this number of children in a node triggers a
         *            redistribution of its children.
         * @return builder
         */
        public Builder minChildren(int minChildren) {
            this.minChildren = of(minChildren);
            return this;
        }

        /**
         * Sets the max number of children in an R-tree node.
         * 
         * @param maxChildren
         *            max number of children in R-tree node.
         * @return builder
         */
        public Builder maxChildren(int maxChildren) {
            this.maxChildren = maxChildren;
            return this;
        }

        /**
         * Sets the {@link Splitter} to use when maxChildren is reached.
         * 
         * @param splitter
         *            node splitting method to use
         * @return builder
         */
        public Builder splitter(Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        public <T> Builder selector(Selector selector) {
            this.selector = selector;
            return this;
        }

        /**
         * Builds the {@link RTree}.
         * 
         * @param <S>
         *            the entry type
         * @return RTree
         */
        public <S> RTree<S> create() {
            if (!minChildren.isPresent())
                minChildren = of(maxChildren / 2);
            return new RTree<S>(new Context(minChildren.get(), maxChildren, selector, splitter));
        }
    }

    /**
     * Adds an entry to the R-tree.
     * 
     * @param entry
     *            item to add to the R-tree.
     * @return a new immutable R-tree including the new entry
     */
    @SuppressWarnings("unchecked")
    public RTree<R> add(Entry<R> entry) {
        if (root.isPresent())
            return new RTree<R>(root.get().add(entry, emptyStack), size + 1, context);
        else
            return new RTree<R>(new Leaf<R>(Lists.newArrayList(entry), context), size + 1, context);
    }

    /**
     * Adds an entry comprised of the given value and Geometry.
     * 
     * @param value
     *            the value of the {@link Entry} to be added
     * @param geometry
     *            the geometry of the {@link Entry} to be added
     * @return a new immutable R-tree including the new entry
     */
    public RTree<R> add(R value, Geometry geometry) {
        return add(Entry.entry(value, geometry));
    }

    /**
     * Delete one entry comprised of the given value and Geometry. This method
     * has no effect if the entry is not present. The entry must match on both
     * value and geometry to be deleted.
     * 
     * @param value
     *            the value of the {@link Entry} to be deleted
     * @param geometry
     *            the geometry of the {@link Entry} to be deleted
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<R> delete(R value, Geometry geometry) {
        return delete(Entry.entry(value, geometry));
    }

    /**
     * Delete one entry if it exists. If multiple copies of the entry are in the
     * R-tree only one will be deleted. The entry must match on both value and
     * geometry to be deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<R> delete(Entry<R> entry) {
        if (root.isPresent()) {
            final Optional<Node<R>> newRoot = root.get().delete(entry, emptyStack);
            if (newRoot.equals(root))
                return this;
            else
                return new RTree<R>(newRoot, size - 1, context);
        } else
            return this;
    }

    /**
     * <p>
     * Returns an Observable sequence of {@link Entry} that satisfy the given
     * condition. Note that this method is well-behaved only if:
     * </p>
     * 
     * <code>condition(g) is true for {@link Geometry} g implies condition(r) is true for the minimum bounding rectangles of the ancestor nodes</code>
     * 
     * <p>
     * <code>distance(g) &lt; sD</code> is an example of such a condition.
     * </p>
     * 
     * @param condition
     *            return Entries whose geometry satisfies the given condition
     * @return sequence of matching entries
     */
    public Observable<Entry<R>> search(Func1<? super Geometry, Boolean> condition) {
        if (root.isPresent())
            return Observable.create(new OnSubscribeSearch<R>(root.get(), condition));
        else
            return Observable.empty();
    }

    /**
     * <p>
     * Returns a comparator that can be used to sort entries returned by search
     * methods. For example:
     * </p>
     * <p>
     * <code>search(100).toSortedList(ascendingDistance(r))</code>
     * </p>
     * 
     * @param r
     *            rectangle to measure distance to
     * @param <S>
     *            the entry type
     * @return a comparator to sort by ascending distance from the rectangle
     */
    public static final <S> Comparator<Entry<S>> ascendingDistance(final Rectangle r) {
        return new Comparator<Entry<S>>() {
            @Override
            public int compare(Entry<S> e1, Entry<S> e2) {
                return ((Double) e1.geometry().distance(r)).compareTo(e2.geometry().distance(r));
            }
        };
    }

    /**
     * Returns a predicate function that indicates if {@link Geometry}
     * intersects with a given rectangle.
     * 
     * @param r
     *            the rectangle to check intersection with
     * @return whether the geometry and the rectangle intersect
     */
    public static Func1<Geometry, Boolean> intersects(final Rectangle r) {
        return new Func1<Geometry, Boolean>() {
            @Override
            public Boolean call(Geometry g) {
                return g.distance(r) == 0;
            }
        };
    }

    /**
     * Returns the always true predicate. See {@link RTree#entries()} for
     * example use.
     */
    public static final Func1<Geometry, Boolean> ALWAYS_TRUE = new Func1<Geometry, Boolean>() {
        @Override
        public Boolean call(Geometry rectangle) {
            return true;
        }
    };

    /**
     * Returns an {@link Observable} sequence of all {@link Entry}s in the
     * R-tree whose minimum bounding rectangle intersects with the given
     * rectangle.
     * 
     * @param r
     *            rectangle to check intersection with the entry mbr
     * @return entries that intersect with the rectangle r
     */
    public Observable<Entry<R>> search(final Rectangle r) {
        return search(intersects(r));
    }

    /**
     * Returns an {@link Observable} sequence of all {@link Entry}s in the
     * R-tree whose minimum bounding rectangles are within maxDistance from the
     * given rectangle.
     * 
     * @param r
     *            rectangle to measure distance from
     * @param maxDistance
     *            entries returned must be within this distance from rectangel r
     * @return the sequence of matching entries
     */
    public Observable<Entry<R>> search(final Rectangle r, final double maxDistance) {
        return search(new Func1<Geometry, Boolean>() {
            @Override
            public Boolean call(Geometry g) {
                return g.distance(r) < maxDistance;
            }
        });
    }

    /**
     * Returns the nearest k entries (k=maxCount) to the given rectangle where
     * the entries are within a given maximum distance from the rectangel.
     * 
     * @param r
     *            rectangle
     * @param maxDistance
     *            max distance of returned entries from the rectangle
     * @param maxCount
     *            max number of entries to return
     * @return nearest entries to maxCount
     */
    public Observable<Entry<R>> nearest(final Rectangle r, final double maxDistance, int maxCount) {
        return search(r, maxDistance)
                .lift(new OperatorBoundedPriorityQueue<Entry<R>>(maxCount, RTree
                        .<R> ascendingDistance(r)));
    }

    /**
     * Returns all entries in the tree as an {@link Observable} sequence.
     * 
     * @return all entries in the R-tree
     */
    public Observable<Entry<R>> entries() {
        return search(ALWAYS_TRUE);
    }

    Visualizer visualize(int width, int height, Rectangle view) {
        return new Visualizer(this, width, height, view);
    }

    Optional<Node<R>> root() {
        return root;
    }

    /**
     * Returns true if and only if the R-tree is empty of entries.
     * 
     * @return is R-tree empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of entries in the RTree.
     * 
     * @return the number of entries
     */
    public int size() {
        return size;
    }

}
