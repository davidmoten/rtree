package com.github.davidmoten.rtree;

public class Context {
	private final int maxChildren;
	private final Splitter splitter;

	public Context(int maxChildren, Splitter splitter) {
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
