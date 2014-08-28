package com.github.davidmoten.rtree;

import com.google.common.base.Preconditions;

public class Context {

	private final int maxChildren;
	private final Splitter splitter;

	public Context(int maxChildren, Splitter splitter) {
		Preconditions.checkNotNull(splitter);
		Preconditions.checkArgument(maxChildren > 2);
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
