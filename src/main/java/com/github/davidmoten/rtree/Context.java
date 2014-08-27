package com.github.davidmoten.rtree;

import com.google.common.base.Preconditions;

public class Context {
	private static final int MAX_CHILDREN_DEFAULT = 4;
	public static final Context DEFAULT = new Context(MAX_CHILDREN_DEFAULT,
			new QuadraticSplitter());

	private final int maxChildren;
	private final Splitter splitter;

	public Context(int maxChildren, Splitter splitter) {
		Preconditions.checkNotNull(splitter);
		this.maxChildren = maxChildren;
		this.splitter = splitter;
	}

	public int maxChildren() {
		return maxChildren;
	}

	public Splitter splitter() {
		return splitter;
	}

}
