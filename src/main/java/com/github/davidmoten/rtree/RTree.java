package com.github.davidmoten.rtree;

import static com.github.davidmoten.rtree.geometry.Geometries.rectangle;
import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rx.operators.OperatorBoundedPriorityQueue;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Immutable in-memory 2D R-Tree with configurable splitter heuristic.
 * 
 * @param <T>
 *            the entry value type
 * @param <S>
 *            the entry geometry type
 */
public final class RTree<T, S extends Geometry> {

    private final Optional<? extends Node<T, S>> root;
    private final Context context;

    /**
     * Benchmarks show that this is a good choice for up to O(10,000) entries
     * when using Quadratic splitter (Guttman).
     */
    public static final int MAX_CHILDREN_DEFAULT_GUTTMAN = 4;

    /**
     * Benchmarks show that this is the sweet spot for up to O(10,000) entries
     * when using R*-tree heuristics.
     */
    public static final int MAX_CHILDREN_DEFAULT_STAR = 4;

    /**
     * Current size in Entries of the RTree.
     */
    private int size;

    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the tree if present
     * @param context
     *            options for the R-tree
     */
    private RTree(Optional<? extends Node<T, S>> root, int size, Context context) {
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
    private RTree(Node<T, S> root, int size, Context context) {
        this(of(root), size, context);
    }

    /**
     * Constructor.
     * 
     * @param context
     *            specifies parameters and behaviour for the R-tree
     */
    private RTree(Context context) {
        this(Optional.<Node<T, S>> absent(), 0, context);
    }

    /**
     * Returns a new Builder instance for {@link RTree}. Defaults to
     * maxChildren=128, minChildren=64, splitter=QuadraticSplitter.
     * 
     * @param <T>
     *            the value type of the entries in the tree
     * @param <S>
     *            the geometry type of the entries in the tree
     * @return a new RTree instance
     */
    public static <T, S extends Geometry> RTree<T, S> create() {
        return new Builder().create();
    }

    /**
     * The tree is scanned for depth and the depth returned. This involves
     * recursing down to the leaf level of the tree to get the current depth.
     * Should be <code>log(n)</code> in complexity.
     * 
     * @return depth of the R-tree
     */
    public int calculateDepth() {
        return calculateDepth(root);
    }

    private static <T, S extends Geometry> int calculateDepth(Optional<? extends Node<T, S>> root) {
        if (!root.isPresent())
            return 0;
        else
            return calculateDepth(root.get(), 0);
    }

    private static <T, S extends Geometry> int calculateDepth(Node<T, S> node, int depth) {
        if (node instanceof Leaf)
            return depth + 1;
        else
            return calculateDepth(((NonLeaf<T, S>) node).children().get(0), depth + 1);
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

    /**
     * Sets the node {@link Selector} which decides which branches to follow
     * when inserting or searching.
     * 
     * @param selector
     *            determines which branches to follow when inserting or
     *            searching
     * @return builder
     */
    public static Builder selector(Selector selector) {
        return new Builder().selector(selector);
    }

    /**
     * Sets the splitter to {@link SplitterRStar} and selector to
     * {@link SelectorRStar} and defaults to minChildren=10.
     * 
     * @return builder
     */
    public static Builder star() {
        return new Builder().star();
    }

    /**
     * RTree Builder.
     */
    public static class Builder {

        /**
         * According to
         * http://dbs.mathematik.uni-marburg.de/publications/myPapers
         * /1990/BKSS90.pdf (R*-tree paper), best filling ratio is 0.4 for both
         * quadratic split and R*-tree split.
         */
        private static final double DEFAULT_FILLING_FACTOR = 0.4;
        private Optional<Integer> maxChildren = absent();
        private Optional<Integer> minChildren = absent();
        private Splitter splitter = new SplitterQuadratic();
        private Selector selector = new SelectorMinimalAreaIncrease();
        private boolean star = false;

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
            this.maxChildren = of(maxChildren);
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

        /**
         * Sets the node {@link Selector} which decides which branches to follow
         * when inserting or searching.
         * 
         * @param selector
         *            selects the branch to follow when inserting or searching
         * @return builder
         */
        public Builder selector(Selector selector) {
            this.selector = selector;
            return this;
        }

        /**
         * Sets the splitter to {@link SplitterRStar} and selector to
         * {@link SelectorRStar} and defaults to minChildren=10.
         * 
         * @return builder
         */
        public Builder star() {
            selector = new SelectorRStar();
            splitter = new SplitterRStar();
            star = true;
            return this;
        }

        /**
         * Builds the {@link RTree}.
         * 
         * @param <T>
         *            value type
         * @param <S>
         *            geometry type
         * @return RTree
         */
        public <T, S extends Geometry> RTree<T, S> create() {
            if (!maxChildren.isPresent())
                if (star)
                    maxChildren = of(MAX_CHILDREN_DEFAULT_STAR);
                else
                    maxChildren = of(MAX_CHILDREN_DEFAULT_GUTTMAN);
            if (!minChildren.isPresent())
                minChildren = of((int) Math.round(maxChildren.get() * DEFAULT_FILLING_FACTOR));
            return new RTree<T, S>(new Context(minChildren.get(), maxChildren.get(), selector,
                    splitter));
        }

    }

    /**
     * Returns an immutable copy of the RTree with the addition of given entry.
     * 
     * @param entry
     *            item to add to the R-tree.
     * @return a new immutable R-tree including the new entry
     */
    @SuppressWarnings("unchecked")
    public RTree<T, S> add(Entry<? extends T, ? extends S> entry) {
        if (root.isPresent()) {
            List<Node<T, S>> nodes = root.get().add(entry);
            Node<T, S> node;
            if (nodes.size() == 1)
                node = nodes.get(0);
            else {
                node = new NonLeaf<T, S>(nodes, context);
            }
            return new RTree<T, S>(node, size + 1, context);
        } else
            return new RTree<T, S>(
                    new Leaf<T, S>(Lists.newArrayList((Entry<T, S>) entry), context), size + 1,
                    context);
    }

    /**
     * Returns an immutable copy of the RTree with the addition of an entry
     * comprised of the given value and Geometry.
     * 
     * @param value
     *            the value of the {@link Entry} to be added
     * @param geometry
     *            the geometry of the {@link Entry} to be added
     * @return a new immutable R-tree including the new entry
     */
    public RTree<T, S> add(T value, S geometry) {
        return add(Entry.entry(value, geometry));
    }

    /**
     * Returns an immutable RTree with the current entries and the additional
     * entries supplied as a parameter.
     * 
     * @param entries
     *            entries to add
     * @return R-tree with entries added
     */
    public RTree<T, S> add(Iterable<Entry<T, S>> entries) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.add(entry);
        return tree;
    }

    /**
     * Returns the Observable sequence of trees created by progressively adding
     * entries.
     * 
     * @param entries
     *            the entries to add
     * @return a sequence of trees
     */
    public Observable<RTree<T, S>> add(Observable<Entry<T, S>> entries) {
        return entries.scan(this, new Func2<RTree<T, S>, Entry<T, S>, RTree<T, S>>() {

            @Override
            public RTree<T, S> call(RTree<T, S> tree, Entry<T, S> entry) {
                return tree.add(entry);
            }
        });
    }

    /**
     * Returns the Observable sequence of trees created by progressively
     * deleting entries.
     * 
     * @param entries
     *            the entries to add
     * @param all
     *            if true delete all matching otherwise just first matching
     * @return a sequence of trees
     */
    public Observable<RTree<T, S>> delete(Observable<Entry<T, S>> entries, final boolean all) {
        return entries.scan(this, new Func2<RTree<T, S>, Entry<T, S>, RTree<T, S>>() {

            @Override
            public RTree<T, S> call(RTree<T, S> tree, Entry<T, S> entry) {
                return tree.delete(entry, all);
            }
        });
    }

    /**
     * Returns a new R-tree with the given entries deleted. If <code>all</code>
     * is false deletes only one if exists. If <code>all</code> is true deletes
     * all matching entries.
     * 
     * @param entries
     *            entries to delete
     * @param all
     *            if false deletes one if exists else deletes all
     * @return R-tree with entries deleted
     */
    public RTree<T, S> delete(Iterable<Entry<T, S>> entries, boolean all) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.delete(entry, all);
        return tree;
    }

    /**
     * Returns a new R-tree with the given entries deleted but only one
     * matching occurence of each entry is deleted. 
     * 
     * @param entries
     *            entries to delete
     * @return R-tree with entries deleted up to one matching occurence 
     *            per entry
     */
    public RTree<T, S> delete(Iterable<Entry<T, S>> entries) {
        RTree<T, S> tree = this;
        for (Entry<T, S> entry : entries)
            tree = tree.delete(entry);
        return tree;
    }

    /**
     * If <code>all</code> is false deletes one entry matching the given value
     * and Geometry. If <code>all</code> is true deletes all entries matching
     * the given value and geometry. This method has no effect if the entry is
     * not present. The entry must match on both value and geometry to be
     * deleted.
     * 
     * @param value
     *            the value of the {@link Entry} to be deleted
     * @param geometry
     *            the geometry of the {@link Entry} to be deleted
     * @param all
     *            if false deletes one if exists else deletes all
     * @return a new immutable R-tree without one or many instances of the
     *         specified entry if it exists otherwise returns the original RTree
     *         object
     */
    public RTree<T, S> delete(T value, S geometry, boolean all) {
        return delete(Entry.entry(value, geometry), all);
    }

    /**
     * Deletes maximum one entry matching the given value and geometry. This
     * method has no effect if the entry is not present. The entry must match on
     * both value and geometry to be deleted.
     * 
     * @param value
     * @param geometry
     * @return an immutable RTree without one entry (if found) matching the
     *         given value and geometry
     */
    public RTree<T, S> delete(T value, S geometry) {
        return delete(Entry.entry(value, geometry), false);
    }

    /**
     * Deletes one or all matching entries depending on the value of
     * <code>all</code>. If multiple copies of the entry are in the R-tree only
     * one will be deleted if all is false otherwise all matching entries will
     * be deleted. The entry must match on both value and geometry to be
     * deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * @param all
     *            if true deletes all matches otherwise deletes first found
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all) {
        if (root.isPresent()) {
            NodeAndEntries<T, S> nodeAndEntries = root.get().delete(entry, all);
            if (nodeAndEntries.node().isPresent() && nodeAndEntries.node().get() == root.get())
                return this;
            else
                return new RTree<T, S>(nodeAndEntries.node(), size - nodeAndEntries.countDeleted()
                        - nodeAndEntries.entriesToAdd().size(), context).add(nodeAndEntries
                        .entriesToAdd());
        } else
            return this;
    }

    /**
     * Deletes one entry if it exists, returning an immutable copy of the RTree
     * without that entry. If multiple copies of the entry are in the R-tree
     * only one will be deleted. The entry must match on both value and geometry
     * to be deleted.
     * 
     * @param entry
     *            the {@link Entry} to be deleted
     * 
     * @param all
     *            if true deletes all matches otherwise deletes first found
     * 
     * @return a new immutable R-tree without one instance of the specified
     *         entry
     */
    public RTree<T, S> delete(Entry<? extends T, ? extends S> entry) {
        return delete(entry, false);
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
    @VisibleForTesting
    Observable<Entry<T, S>> search(Func1<? super Geometry, Boolean> condition) {
        if (root.isPresent())
            return Observable.create(new OnSubscribeSearch<T, S>(root.get(), condition));
        else
            return Observable.empty();
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
                return g.intersects(r);
            }
        };
    }

    /**
     * Returns the always true predicate. See {@link RTree#entries()} for
     * example use.
     */
    private static final Func1<Geometry, Boolean> ALWAYS_TRUE = new Func1<Geometry, Boolean>() {
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
    public Observable<Entry<T, S>> search(final Rectangle r) {
        return search(intersects(r));
    }

    /**
     * Returns an {@link Observable} sequence of all {@link Entry}s in the
     * R-tree whose minimum bounding rectangle intersects with the given point.
     * 
     * @param p
     *            point to check intersection with the entry mbr
     * @return entries that intersect with the point p
     */
    public Observable<Entry<T, S>> search(final Point p) {
        return search(p.mbr());
    }

    /**
     * Returns an {@link Observable} sequence of all {@link Entry}s in the
     * R-tree whose minimum bounding rectangles are strictly less than
     * maxDistance from the given rectangle.
     * 
     * @param r
     *            rectangle to measure distance from
     * @param maxDistance
     *            entries returned must be within this distance from rectangle r
     * @return the sequence of matching entries
     */
    public Observable<Entry<T, S>> search(final Rectangle r, final double maxDistance) {
        return search(new Func1<Geometry, Boolean>() {
            @Override
            public Boolean call(Geometry g) {
                return g.distance(r) < maxDistance;
            }
        });
    }

    /**
     * Returns the intersections with the the given (arbitrary) geometry using
     * an intersection function to filter the search results returned from a
     * search of the mbr of <code>g</code>.
     * 
     * @param <R>
     *            type of geometry being searched for intersection with
     * @param g
     *            geometry being searched for intersection with
     * @param intersects
     *            function to determine if the two geometries intersect
     * @return a sequence of entries that intersect with g
     */
    public <R extends Geometry> Observable<Entry<T, S>> search(final R g,
            final Func2<? super S, ? super R, Boolean> intersects) {
        return search(g.mbr()).filter(new Func1<Entry<T, S>, Boolean>() {
            @Override
            public Boolean call(Entry<T, S> entry) {
                return intersects.call(entry.geometry(), g);
            }
        });
    }

    /**
     * Returns all entries strictly less than <code>maxDistance</code> from the
     * given geometry. Because the geometry may be of an arbitrary type it is
     * necessary to also pass a distance function.
     * 
     * @param <R>
     *            type of the geometry being searched for
     * @param g
     *            geometry to search for entries within maxDistance of
     * @param maxDistance
     *            strict max distance that entries must be from g
     * @param distance
     *            function to calculate the distance between geometries of type
     *            S and R.
     * @return entries strictly less than maxDistance from g
     */
    public <R extends Geometry> Observable<Entry<T, S>> search(final R g, final double maxDistance,
            final Func2<? super S, ? super R, Double> distance) {
        return search(new Func1<Geometry, Boolean>() {
            @Override
            public Boolean call(Geometry entry) {
                // just use the mbr initially
                return entry.distance(g.mbr()) < maxDistance;
            }
        })
        // refine with distance function
        .filter(new Func1<Entry<T, S>, Boolean>() {
            @Override
            public Boolean call(Entry<T, S> entry) {
                return distance.call(entry.geometry(), g) < maxDistance;
            }
        });
    }

    /**
     * Returns an {@link Observable} sequence of all {@link Entry}s in the
     * R-tree whose minimum bounding rectangles are within maxDistance from the
     * given point.
     * 
     * @param p
     *            point to measure distance from
     * @param maxDistance
     *            entries returned must be within this distance from point p
     * @return the sequence of matching entries
     */
    public Observable<Entry<T, S>> search(final Point p, final double maxDistance) {
        return search(p.mbr(), maxDistance);
    }

    /**
     * Returns the nearest k entries (k=maxCount) to the given rectangle where
     * the entries are strictly less than a given maximum distance from the
     * rectangle.
     * 
     * @param r
     *            rectangle
     * @param maxDistance
     *            max distance of returned entries from the rectangle
     * @param maxCount
     *            max number of entries to return
     * @return nearest entries to maxCount, not in any particular order
     */
    public Observable<Entry<T, S>> nearest(final Rectangle r, final double maxDistance, int maxCount) {
        return search(r, maxDistance).lift(
                new OperatorBoundedPriorityQueue<Entry<T, S>>(maxCount, Comparators
                        .<T, S> ascendingDistance(r)));
    }

    /**
     * Returns the nearest k entries (k=maxCount) to the given point where the
     * entries are strictly less than a given maximum distance from the point.
     * 
     * @param p
     *            point
     * @param maxDistance
     *            max distance of returned entries from the point
     * @param maxCount
     *            max number of entries to return
     * @return nearest entries to maxCount, not in any particular order
     */
    public Observable<Entry<T, S>> nearest(final Point p, final double maxDistance, int maxCount) {
        return nearest(p.mbr(), maxDistance, maxCount);
    }

    /**
     * Returns all entries in the tree as an {@link Observable} sequence.
     * 
     * @return all entries in the R-tree
     */
    public Observable<Entry<T, S>> entries() {
        return search(ALWAYS_TRUE);
    }

    /**
     * Returns a {@link Visualizer} for an image of given width and height and
     * restricted to the given view of the coordinates. The points in the view
     * are scaled to match the aspect ratio defined by the width and height.
     * 
     * @param width
     *            of the image in pixels
     * @param height
     *            of the image in pixels
     * @param view
     *            using the coordinate system of the entries
     * @return visualizer
     */
    @SuppressWarnings("unchecked")
    public Visualizer visualize(int width, int height, Rectangle view) {
        return new Visualizer((RTree<?, Geometry>) this, width, height, view);
    }

    /**
     * Returns a {@link Visualizer} for an image of given width and height and
     * restricted to the the smallest view that fully contains the coordinates.
     * The points in the view are scaled to match the aspect ratio defined by
     * the width and height.
     * 
     * @param width
     *            of the image in pixels
     * @param height
     *            of the image in pixels
     * @return visualizer
     */
    public Visualizer visualize(int width, int height) {
        return visualize(width, height, calculateMaxView(this));
    }

    private Rectangle calculateMaxView(RTree<T, S> tree) {
        return tree
                .entries()
                .reduce(Optional.<Rectangle> absent(),
                        new Func2<Optional<Rectangle>, Entry<T, S>, Optional<Rectangle>>() {

                            @Override
                            public Optional<Rectangle> call(Optional<Rectangle> r, Entry<T, S> entry) {
                                if (r.isPresent())
                                    return of(r.get().add(entry.geometry().mbr()));
                                else
                                    return of(entry.geometry().mbr());
                            }
                        }).toBlocking().single().or(rectangle(0, 0, 0, 0));
    }

    Optional<? extends Node<T, S>> root() {
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

    /**
     * Returns a {@link Context} containing the configuration of the RTree at
     * the time of instantiation.
     * 
     * @return the configuration of the RTree prior to instantiation
     */
    public Context context() {
        return context;
    }

    /**
     * Returns a human readable form of the RTree. Here's an example:
     * 
     * <pre>
     * mbr=Rectangle [x1=10.0, y1=4.0, x2=62.0, y2=85.0]
     *   mbr=Rectangle [x1=28.0, y1=4.0, x2=34.0, y2=85.0]
     *     entry=Entry [value=2, geometry=Point [x=29.0, y=4.0]]
     *     entry=Entry [value=1, geometry=Point [x=28.0, y=19.0]]
     *     entry=Entry [value=4, geometry=Point [x=34.0, y=85.0]]
     *   mbr=Rectangle [x1=10.0, y1=45.0, x2=62.0, y2=63.0]
     *     entry=Entry [value=5, geometry=Point [x=62.0, y=45.0]]
     *     entry=Entry [value=3, geometry=Point [x=10.0, y=63.0]]
     * </pre>
     * 
     * @return a string representation of the RTree
     */
    public String asString() {
        if (!root.isPresent())
            return "";
        else
            return asString(root.get(), "");
    }

    private String asString(Node<T, S> node, String margin) {
        final String marginIncrement = "  ";
        StringBuilder s = new StringBuilder();
        if (node instanceof NonLeaf) {
            s.append(margin);
            s.append("mbr=" + node.geometry());
            s.append('\n');
            NonLeaf<T, S> n = (NonLeaf<T, S>) node;
            for (Node<T, S> child : n.children()) {
                s.append(asString(child, margin + marginIncrement));
            }
        } else {
            Leaf<T, S> leaf = (Leaf<T, S>) node;
            s.append(margin);
            s.append("mbr=");
            s.append(leaf.geometry());
            s.append('\n');
            for (Entry<T, S> entry : leaf.entries()) {
                s.append(margin);
                s.append(marginIncrement);
                s.append("entry=");
                s.append(entry);
                s.append('\n');
            }
        }
        return s.toString();
    }

}
