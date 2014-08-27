package com.github.davidmoten.rtree;

import rx.Observable;

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
		this(Optional.of(root), context);
	}

	public RTree() {
		this(Optional.<Node> absent(), Context.DEFAULT);
	}

	public RTree add(Entry entry) {
		if (root.isPresent())
			return new RTree(root.get().add(entry,
					ImmutableStack.<NonLeaf> empty()), context);
		else
			return new RTree(new Leaf(Lists.newArrayList(entry), context),
					context);
	}

	public Observable<Entry> search(Rectangle r) {
		if (root.isPresent())
			return Observable.create(new OnSubscribeSearch(root.get(), r));
		else
			return Observable.empty();
	}

	public Observable<Entry> entries() {
		if (root.isPresent())
			return Observable.create(new OnSubscribeEntries(root.get()));
		else
			return Observable.empty();
	}

	public Visualizer visualize(int width, int height, Rectangle view,
			int maxDepth) {
		return new Visualizer(this, width, height, view, maxDepth);
	}

	public Optional<Node> root() {
		return root;
	}

}
