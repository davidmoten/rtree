package com.github.davidmoten.rtree;

public class Context {
	private final int maxChildren;
	private final EntiriesSplitter splitter;

	public Context(int maxChildren, EntiriesSplitter splitter) {
		this.maxChildren = maxChildren;
		this.splitter = splitter;
	}

	public int maxChildren() {
		return maxChildren;
	}

	public EntiriesSplitter splitter() {
		return splitter;
	}

}
