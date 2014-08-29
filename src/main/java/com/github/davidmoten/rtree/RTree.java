package com.github.davidmoten.rtree;

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
 * Immutable in-memory R-Tree with configurable splitter heuristic.
 */
public class RTree<R> {

	private final ImmutableStack<NonLeaf<R>> emptyStack = ImmutableStack
			.<NonLeaf<R>> empty();

	private static final int MAX_CHILDREN_DEFAULT = 32;

	private final Optional<Node<R>> root;
	private final Context context;

	/**
	 * Constructor.
	 * 
	 * @param root
	 * @param context
	 */
	private RTree(Optional<Node<R>> root, Context context) {
		this.root = root;
		this.context = context;
	}

	/**
	 * Constructor.
	 * 
	 * @param root
	 * @param context
	 */
	private RTree(Node<R> root, Context context) {
		this(of(root), context);
	}

	/**
	 * Constructor. Creates an empty R-tree with maxChildren at 32 and using a
	 * {@link QuadraticSplitter}.
	 */
	public RTree() {
		this(Optional.<Node<R>> absent(), new Context(MAX_CHILDREN_DEFAULT,
				new QuadraticSplitter()));
	}

	/**
	 * Constructor. Creates an empty R-tree using specified maxChildren and a
	 * {@link QuadraticSplitter}.
	 * 
	 * @param maxChildren
	 */
	public RTree(int maxChildren) {
		this(Optional.<Node<R>> absent(), new Context(maxChildren,
				new QuadraticSplitter()));
	}

	/**
	 * Constructor. Creates an empty R-tree using specified maxChildren and
	 * splitter.
	 * 
	 * @param maxChildren
	 * @param splitter
	 *            for example {@link QuadraticSplitter}
	 */
	public RTree(int maxChildren, Splitter splitter) {
		this(Optional.<Node<R>> absent(), new Context(maxChildren, splitter));
	}

	/**
	 * Returns a new Builder instance for {@link RTree}.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * RTree Builder
	 */
	public static class Builder {

		private int maxChildren = 8;
		private Splitter splitter = new QuadraticSplitter();

		private Builder() {
		}

		/**
		 * Sets the max number of children in an R-tree node.
		 * 
		 * @param maxChildren
		 * @return
		 */
		public Builder maxChildren(int maxChildren) {
			this.maxChildren = maxChildren;
			return this;
		}

		/**
		 * Sets the {@link Splitter} to use when maxChildren is reached.
		 * 
		 * @param splitter
		 * @return
		 */
		public Builder splitter(Splitter splitter) {
			this.splitter = splitter;
			return this;
		}

		/**
		 * Builds the {@link RTree}.
		 */
		public <S> RTree<S> build() {
			return new RTree<S>(maxChildren, splitter);
		}
	}

	/**
	 * Adds an entry to the R-tree.
	 * 
	 * @param entry
	 *            item to add to the R-tree.
	 * @return a new immutable R-trees
	 */
	@SuppressWarnings("unchecked")
	public RTree<R> add(Entry<R> entry) {
		if (root.isPresent())
			return new RTree<R>(root.get().add(entry, emptyStack), context);
		else
			return new RTree<R>(
					new Leaf<R>(Lists.newArrayList(entry), context), context);
	}

	/**
	 * Adds an {@link Entry} comprised of the object and the given geometry to
	 * the RTree.
	 * 
	 * @param object
	 * @param geometry
	 * @return
	 */
	public RTree<R> add(R object, Geometry geometry) {
		return add(new Entry<R>(object, geometry));
	}

	public RTree<R> delete(Entry<R> entry) {
		if (root.isPresent()) {
			return new RTree<R>(root.get().delete(entry, emptyStack), context);
		} else
			return this;
	}

	/**
	 * Returns an Observable sequence of {@link Entry} where the criterion is
	 * satisfied both for the returned entries and the minimum bounding
	 * rectangles in the ancestor nodes.
	 * 
	 * @param criterion
	 * @return
	 */
	public Observable<Entry<R>> search(
			Func1<? super Geometry, Boolean> criterion) {
		if (root.isPresent())
			return Observable.create(new OnSubscribeSearch<R>(root.get(),
					criterion));
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
	 * @return
	 */
	public static final <S> Comparator<Entry<S>> ascendingDistance(
			final Rectangle r) {
		return new Comparator<Entry<S>>() {
			@Override
			public int compare(Entry<S> e1, Entry<S> e2) {
				return ((Double) e1.geometry().distance(r)).compareTo(e2
						.geometry().distance(r));
			}
		};
	}

	public static Func1<Geometry, Boolean> intersects(final Rectangle r) {
		return new Func1<Geometry, Boolean>() {
			@Override
			public Boolean call(Geometry g) {
				return g.intersects(r);
			}
		};
	}

	public static Func1<Geometry, Boolean> ALWAYS_TRUE = new Func1<Geometry, Boolean>() {
		@Override
		public Boolean call(Geometry rectangle) {
			return true;
		}
	};

	public Observable<Entry<R>> search(final Rectangle r) {
		return search(intersects(r));
	}

	public Observable<Entry<R>> search(final Rectangle r,
			final double maxDistance) {
		return search(new Func1<Geometry, Boolean>() {
			@Override
			public Boolean call(Geometry g) {
				return g.distance(r) < maxDistance;
			}
		});
	}

	public Observable<Entry<R>> nearest(final Rectangle r,
			final double maxDistance, int maxCount) {
		return search(r, maxDistance).lift(
				new OperatorBoundedPriorityQueue<Entry<R>>(maxCount, RTree
						.<R> ascendingDistance(r)));
	}

	public Observable<Entry<R>> entries() {
		return search(ALWAYS_TRUE);
	}

	public Visualizer visualize(int width, int height, Rectangle view,
			int maxDepth) {
		return new Visualizer(this, width, height, view);
	}

	Optional<Node<R>> root() {
		return root;
	}

}
