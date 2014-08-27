package com.github.davidmoten.rtree;

import java.util.List;

import com.github.davidmoten.util.ImmutableStack;
import com.google.common.base.Preconditions;

public class NonLeaf implements Node {

	private final List<? extends Node> children;
	private final Rectangle mbr;

	public NonLeaf(List<? extends Node> children) {
		Preconditions.checkArgument(!children.isEmpty());
		this.children = children;
		this.mbr = Util.mbr(children);
	}

	public List<? extends Node> children() {
		return children;
	}

	@Override
	public Rectangle mbr() {
		return mbr;
	}

	@Override
	public Node add(Entry entry, ImmutableStack<NonLeaf> stack) {
		final HasMbr child = Util.findLeastIncreaseInMbrArea(entry.mbr(), children);
		return ((Node) child).add(entry, stack.push(this));
	}
}
