package com.github.davidmoten.rtree;

import java.util.List;
import java.util.Stack;

import com.google.common.base.Preconditions;

public class NonLeaf implements Node {

	private final List<Node> children;
	private final Rectangle mbr;
	private final Context context;

	public NonLeaf(List<Node> children, Context context) {
		Preconditions.checkArgument(!children.isEmpty());
		this.children = children;
		this.mbr = Util.mbr(children);
		this.context = context;
	}

	public List<Node> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public NonLeaf add(Entry entry, Stack<NonLeaf> stack) {
		return null;
	}
}
