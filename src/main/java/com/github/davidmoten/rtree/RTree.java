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

public class RTree {

	private final Optional<Node> root;
	private final Context context;

	private RTree(Optional<Node> root, Context context) {
		this.root = root;
		this.context = context;
	}

	private RTree(Node root, Context context) {
		this(of(root), context);
	}

	public RTree() {
		this(Optional.<Node> absent(), Context.DEFAULT);
	}

	public RTree(int maxChildren) {
		this(Optional.<Node> absent(), new Context(maxChildren,
				new QuadraticSplitter()));
	}

	public RTree(int maxChildren, Splitter splitter) {
		this(Optional.<Node> absent(), new Context(maxChildren, splitter));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private int maxChildren = 8;
		private Splitter splitter = new QuadraticSplitter();

		private Builder() {
		}

		public Builder maxChildren(int maxChildren) {
			this.maxChildren = maxChildren;
			return this;
		}

		public Builder splitter(Splitter splitter) {
			this.splitter = splitter;
			return this;
		}

		public RTree build() {
			return new RTree(maxChildren, splitter);
		}
	}

	public RTree add(Entry entry) {
		if (root.isPresent())
			return new RTree(root.get().add(entry,
					ImmutableStack.<NonLeaf> empty()), context);
		else
			return new RTree(new Leaf(Lists.newArrayList(entry), context),
					context);
	}

	public RTree add(Object object, Rectangle mbr) {
		return add(new Entry(object, mbr));
	}

	public Observable<Entry> search(Func1<? super Geometry, Boolean> criterion) {
		if (root.isPresent())
			return Observable.create(new OnSubscribeSearch(root.get(),
					criterion));
		else
			return Observable.empty();
	}

	public static final Comparator<Entry> ascendingDistance(final Rectangle r) {
		return new Comparator<Entry>() {
			@Override
			public int compare(Entry e1, Entry e2) {
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

	public Observable<Entry> search(final Rectangle r) {
		return search(intersects(r));
	}

	public Observable<Entry> search(final Rectangle r, final double maxDistance) {
		return search(new Func1<Geometry, Boolean>() {
			@Override
			public Boolean call(Geometry g) {
				return g.distance(r) < maxDistance;
			}
		});
	}

	public Observable<Entry> nearest(final Rectangle r,
			final double maxDistance, int maxCount) {
		return search(r, maxDistance).lift(
				new OperatorBoundedPriorityQueue<Entry>(maxCount,
						ascendingDistance(r)));
	}

	public Observable<Entry> entries() {
		return search(ALWAYS_TRUE);
	}

	public Visualizer visualize(int width, int height, Rectangle view,
			int maxDepth) {
		return new Visualizer(this, width, height, view);
	}

	Optional<Node> root() {
		return root;
	}

}
